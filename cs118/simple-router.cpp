/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/**
 * Copyright (c) 2017 Alexander Afanasyev
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version
 * 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

#include "simple-router.hpp"
#include "core/utils.hpp"

#include <fstream>

namespace simple_router {

//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
// IMPLEMENT THIS METHOD
void
SimpleRouter::handlePacket(const Buffer& packet, const std::string& inIface)
{
  std::cerr << "Got packet of size " << packet.size() << " on interface " << inIface << std::endl;

  const Interface* iface = findIfaceByName(inIface);
  if (iface == nullptr) {
    std::cerr << "Received packet, but interface is unknown, ignoring" << std::endl;
    return;
  }

  std::cerr << getRoutingTable() << std::endl;
  const uint8_t* buf = packet.data();
  const ethernet_hdr *ehdr = reinterpret_cast<const ethernet_hdr *>(buf);
  uint16_t ethtype = ethertype(buf);
  //print_hdrs(packet);
  macToString(iface->addr);
  std::cerr << ipToString(iface->ip) << std::endl;
  if (ethtype == ethertype_arp){ // ARP

      const uint8_t* arp_buf = buf + sizeof(ethernet_hdr);
      const arp_hdr *hdr = reinterpret_cast<const arp_hdr*>(arp_buf);
      unsigned short operation = ntohs(hdr->arp_op);

      if(operation == arp_op_request){ // Request
          ipToString(iface->ip);
          //fprintf(stderr, "%d", iface->addr);
          size_t packet_length = sizeof(arp_hdr) + sizeof(ethernet_hdr);
          ethernet_hdr *new_ethernet_hdr = new ethernet_hdr();
          //new_ethernet_hdr->ether_shost = ehdr->ether_dhost;
          //new_ethernet_hdr->ether_dhost = ;
          //new_ethernet_hdr->ether_dhost = ehdr->ether_type;
          arp_hdr *new_arp_hdr = new arp_hdr();
          new_arp_hdr->arp_hrd = hdr->arp_hrd;
          new_arp_hdr->arp_pro = hdr->arp_pro;
          new_arp_hdr->arp_hln = hdr->arp_hln ;
          new_arp_hdr->arp_pln = hdr->arp_pln ;
          new_arp_hdr->arp_op =  htons(2);
          new_arp_hdr->arp_sha = iface->addr;
          new_arp_hdr->arp_sip = hdr->arp_ip;
          new_arp_hdr->arp_tha = hdr->arp_sha;
          new_arp_hdr->arp_tip = hdr->arp_sip;

      }
      else if (operation == arp_op_reply) { //Reply
      }
      //fprintf(stderr, "\tOpcode: %d\n", htons(hdr->arp_op));
  }
  else if (ethtype == ethertype_ip)  // IPv4
      return;
  //const uint8_t* test = packet.data()
  //const ethernet_hdr *ehdr = reinterpret_cast<const ethernet_hdr *>(test);
  //std::cerr << "Ethernet Type: " << ethtype << std::endl;
  //std::cerr << "Ethernet Header Source: " << ehdr->ether_shost << std::endl;
  //std::cerr << "Ethernet Header Type: " << ntohs(ehdr->ether_type) << std::endl;
  //print_hdrs(packet);
  //std::cerr << "Buffer Data: " << packet.data() << std::endl;
  // FILL THIS IN

}
//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////

// You should not need to touch the rest of this code.
SimpleRouter::SimpleRouter()
  : m_arp(*this)
{
}

void
SimpleRouter::sendPacket(const Buffer& packet, const std::string& outIface)
{
  m_pox->begin_sendPacket(packet, outIface);
}

bool
SimpleRouter::loadRoutingTable(const std::string& rtConfig)
{
  return m_routingTable.load(rtConfig);
}

void
SimpleRouter::loadIfconfig(const std::string& ifconfig)
{
  std::ifstream iff(ifconfig.c_str());
  std::string line;
  while (std::getline(iff, line)) {
    std::istringstream ifLine(line);
    std::string iface, ip;
    ifLine >> iface >> ip;

    in_addr ip_addr;
    if (inet_aton(ip.c_str(), &ip_addr) == 0) {
      throw std::runtime_error("Invalid IP address `" + ip + "` for interface `" + iface + "`");
    }

    m_ifNameToIpMap[iface] = ip_addr.s_addr;
  }
}

void
SimpleRouter::printIfaces(std::ostream& os)
{
  if (m_ifaces.empty()) {
    os << " Interface list empty " << std::endl;
    return;
  }

  for (const auto& iface : m_ifaces) {
    os << iface << "\n";
  }
  os.flush();
}

const Interface*
SimpleRouter::findIfaceByIp(uint32_t ip) const
{
  auto iface = std::find_if(m_ifaces.begin(), m_ifaces.end(), [ip] (const Interface& iface) {
      return iface.ip == ip;
    });

  if (iface == m_ifaces.end()) {
    return nullptr;
  }

  return &*iface;
}

const Interface*
SimpleRouter::findIfaceByMac(const Buffer& mac) const
{
  auto iface = std::find_if(m_ifaces.begin(), m_ifaces.end(), [mac] (const Interface& iface) {
      return iface.addr == mac;
    });

  if (iface == m_ifaces.end()) {
    return nullptr;
  }

  return &*iface;
}

const Interface*
SimpleRouter::findIfaceByName(const std::string& name) const
{
  auto iface = std::find_if(m_ifaces.begin(), m_ifaces.end(), [name] (const Interface& iface) {
      return iface.name == name;
    });

  if (iface == m_ifaces.end()) {
    return nullptr;
  }

  return &*iface;
}

void
SimpleRouter::reset(const pox::Ifaces& ports)
{
  std::cerr << "Resetting SimpleRouter with " << ports.size() << " ports" << std::endl;

  m_arp.clear();
  m_ifaces.clear();

  for (const auto& iface : ports) {
    auto ip = m_ifNameToIpMap.find(iface.name);
    if (ip == m_ifNameToIpMap.end()) {
      std::cerr << "IP_CONFIG missing information about interface `" + iface.name + "`. Skipping it" << std::endl;
      continue;
    }

    m_ifaces.insert(Interface(iface.name, iface.mac, ip->second));
  }

  printIfaces(std::cerr);
}


} // namespace simple_router {

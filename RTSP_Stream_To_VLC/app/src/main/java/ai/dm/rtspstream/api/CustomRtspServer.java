package ai.dm.rtspstream.api;

/**
 * Created by earleaguilar on 4/11/18.
 */

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.rtsp.RtspServer;

public class CustomRtspServer extends RtspServer {
    public CustomRtspServer() {
        super();
        // RTSP server disabled by default
        mEnabled = false;
    }
}
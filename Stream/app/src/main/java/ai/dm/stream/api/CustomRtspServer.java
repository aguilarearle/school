package ai.dm.stream.api;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.rtsp.RtspServer;

/**
 * Created by earleaguilar on 4/11/18.
 */

public class CustomRtspServer extends RtspServer{
    public CustomRtspServer(){
        super();

        // Disabled by default
        mEnabled = false;
    }
}

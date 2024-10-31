package handlers;

import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WebSocketSessions {
    private Map<Integer, Set<Session>> sessionMap = new HashMap<>();

    public void addSession(int gameID, Session session) {
        if (sessionMap.get(gameID) == null) {
            sessionMap.put(gameID, new HashSet<>());
            sessionMap.get(gameID).add(session);
        } else {
            sessionMap.get(gameID).add(session);
        }
    }
//    public void removeSessionFromGame(int gameID, Session session) {
//        sessionMap.get(gameID).remove(session);
//    }
    public void removeSession(Session session) {

    }
    public Map<Integer, Set<Session>> getSessions() {
        return sessionMap;
    }


}

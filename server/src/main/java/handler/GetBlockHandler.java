package handler;

import dao.ConversationDao;
import org.bson.Document;
import request.ParsedRequest;
import response.HttpResponseBuilder;
import response.RestApiAppResponse;

public class GetBlockHandler implements BaseHandler{

    @Override
    public HttpResponseBuilder handleRequest(ParsedRequest request) {
        AuthFilter.AuthResult authResult = AuthFilter.doFilter(request);
        if(!authResult.isLoggedIn){
            return new HttpResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }
        ConversationDao conversationDao = ConversationDao.getInstance();
        var filter = new Document("userName", authResult.userName).append("blockStatus", true);
        var res = new RestApiAppResponse<>(true, conversationDao.query(filter), null);
        return new HttpResponseBuilder().setStatus("200 OK").setBody(res);
    }
}

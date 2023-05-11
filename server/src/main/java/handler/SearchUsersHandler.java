package handler;

import dao.UserDao;
import request.ParsedRequest;
import response.HttpResponseBuilder;
import response.RestApiAppResponse;

//SearchUsers Handler is needed
public class SearchUsersHandler implements BaseHandler {
    public HttpResponseBuilder handleRequest(ParsedRequest request){
        UserDao userDao = UserDao.getInstance();
        String search = request.getQueryParam("search");
        var res = new RestApiAppResponse<>(true, userDao.searchUsers(search), null);
        return new HttpResponseBuilder().setStatus("200 OK").setBody(res);
    }
}

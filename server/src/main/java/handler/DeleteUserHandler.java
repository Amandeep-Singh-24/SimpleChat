package handler;

import dao.UserDao;
import org.bson.Document;
import request.ParsedRequest;
import response.HttpResponseBuilder;
import response.RestApiAppResponse;

public class DeleteUserHandler implements BaseHandler {

  @Override
  public HttpResponseBuilder handleRequest(ParsedRequest request) {
    // Get an instance of UserDao
    UserDao userDao = UserDao.getInstance();
    // Extract the "userName" query parameter from the request
    String userName = request.getQueryParam("userName");
    // Create a query document to find the user by "userName"
    var query = new Document("userName", userName);
    RestApiAppResponse res;
    var resultQ = userDao.query(query);
    if (resultQ.size() == 0) {
      // User not found in the database
      res = new RestApiAppResponse<>(false, null, "User not found");
    } else {
      // User found, proceed with deletion
      userDao.deleteUser(query);
      res = new RestApiAppResponse<>(true, null, "User deleted");
    }
    return new HttpResponseBuilder().setStatus("200 OK").setBody(res);
  }
}

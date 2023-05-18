package handler;

import request.ParsedRequest;

public class HandlerFactory {
  // routes based on the path. Add your custom handlers here
  public static BaseHandler getHandler(ParsedRequest request) {
    switch (request.getPath()) {
      case "/createUser":
        return new CreateUserHandler();
      case "/login":
        return new LoginHandler();
      case "/getConversations":
        return new GetConversationsHandler();
      case "/getConversation":
        return new GetConversationHandler();
      case "/createMessage":
        return new CreateMessageHandler();

      case "/createMessage2":
        return new CreateMessageHandler();

      // new endpoint added  
      case "/deleteUser":
        return new DeleteUserHandler();
      //New path needed for searchUsers
       case "/searchUsers":
         return new SearchUsersHandler();
         case "/createMessage3":
        return new CreateMessageHandler();
      default:
        return new FallbackHandler();
    }
  }

}

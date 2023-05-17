package handler;

import dao.ConversationDao;
import dao.MessageDao;
import dao.UserDao;
import dto.ConversationDto;
import dto.MessageDto;
import handler.AuthFilter.AuthResult;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.Document;
import request.ParsedRequest;
import response.CustomHttpResponse;
import response.HttpResponseBuilder;
import response.RestApiAppResponse;

public class CreateMessageHandler implements BaseHandler {

  @Override
  public HttpResponseBuilder handleRequest(ParsedRequest request) {
    //System.out.println(request.getBody());
    MessageDto messageDto = GsonTool.gson.fromJson(request.getBody(), dto.MessageDto.class);
    MessageDao messageDao = MessageDao.getInstance();
    UserDao userDao = UserDao.getInstance();

    AuthResult authResult = AuthFilter.doFilter(request);
    if(!authResult.isLoggedIn){
      System.out.println("Not Logged in");
      return new HttpResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
    }

    //make sure every name is valid
    for(String toUsername: messageDto.getToId()){
      if (userDao.query(new Document("userName", toUsername)).size() == 0) {
        var res = new RestApiAppResponse<>(false, null,
                "Sending message to unknown user");
        return new HttpResponseBuilder().setStatus("200 OK").setBody(res);
      }
    }

    //Create array that has everyone in it
    String[] people = new String[messageDto.getToId().length + 1];
    people[0] = messageDto.getFromId();
    for(int x = 1; x <= messageDto.getToId().length; x++){
        people[x] = messageDto.getToId()[x-1];
    }

    String conversationId = makeConvoId(people);

    ConversationDao conversationDao = ConversationDao.getInstance();
    messageDto.setConversationId(conversationId);
    messageDto.setFromId(authResult.userName);
    messageDao.put(messageDto);


    if(conversationDao.query(new Document("conversationId", conversationId)).size() == 0){
      conversationDao.put(createConvoDao(messageDto.getFromId(), conversationId));
      for(String toUsername: messageDto.getToId()){
        conversationDao.put(createConvoDao(toUsername, conversationId));
      }
    }

    System.out.println("Message Created");

    var res = new RestApiAppResponse<>(true, List.of(messageDto), null);
    return new HttpResponseBuilder().setStatus("200 OK").setBody(res);
  }

  public static String makeConvoId(String[] people){
    people = Stream.of(people)
            .sorted()
            .toArray(String[]::new);

    return String.join("_", people);
  }

  public ConversationDto createConvoDao(String userName, String convoId){
      ConversationDto convo = new ConversationDto();
      convo.setUserName(userName);
      convo.setConversationId(convoId);
      return convo;
  }

}

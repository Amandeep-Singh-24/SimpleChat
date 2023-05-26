package handler;

import dao.ConversationDao;
import dto.ConversationDto;
import org.bson.Document;
import request.ParsedRequest;
import response.HttpResponseBuilder;
import response.RestApiAppResponse;


import java.util.List;

public class BlockUserHandler implements BaseHandler{
    @Override
    public HttpResponseBuilder handleRequest(ParsedRequest request) {

        AuthFilter.AuthResult authResult= AuthFilter.doFilter(request);
        if(!authResult.isLoggedIn) {
            return new HttpResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }
            ConversationDao conversationDao = ConversationDao.getInstance();
            ConversationDto conversationDto = new ConversationDto();
            conversationDto.setUserName(authResult.userName);
            conversationDto.setBlockedUser(request.getQueryParam("blockedUser"));
//            conversationDto.setConversationId(CreateMessageHandler.makeConvoId(conversationDto.getUserName(),conversationDto.getBlockedUser());
            var alreadyBlocked = conversationDao.query(new Document("userName", conversationDto.getBlockedUser())
                    .append("conversationId", conversationDto.getConversationId()));
            if(alreadyBlocked.size() > 0 && alreadyBlocked.get(0).isBlocked()){
                var res = new RestApiAppResponse<>( false, null, "user has already been blocked you");
                return new HttpResponseBuilder().setStatus("200 OK").setBody(res);
            }

            ConversationDto conversationDtoUpdate = new ConversationDto();
            conversationDtoUpdate.setUserName(conversationDto.getUserName());
            conversationDtoUpdate.setBlockedUser(conversationDto.getBlockedUser());
            conversationDtoUpdate.setfriendName(conversationDto.getBlockedUser());
            conversationDtoUpdate.setConversationId(conversationDto.getConversationId());
            boolean currentBlockStatus = conversationDao.query(new Document("userName", authResult.userName).
                    append("conversationId", conversationDto.getConversationId())).get(0).isBlocked();
            conversationDtoUpdate.setBolckedStatus(!currentBlockStatus);
//            conversationDao.remove(conversationDao.query(new Document("userName", conversationDto.getUserName()).
//                    append("conversationId", conversationDto.getConversationId())).get(0));
            conversationDao.put(conversationDtoUpdate);
            String msg = "User" + conversationDtoUpdate.getBlockedUser();
            if (conversationDtoUpdate.isBlocked()){
                msg = msg + "is blocked";
            } else{
                msg = msg + "is unblocked";
            }
            var res = new RestApiAppResponse<>( true, List.of(conversationDtoUpdate), msg);
            return new HttpResponseBuilder().setStatus("200 OK").setBody(res);

        }

    }

package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import dto.UserDto;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.Document;

public class UserDao extends BaseDao<UserDto> {

  private static UserDao instance;

  private UserDao(MongoCollection<Document> collection){
    super(collection);
  }

  public static UserDao getInstance(){
    if(instance != null){
      return instance;
    }
    instance = new UserDao(MongoConnection.getCollection("UserDao"));
    return instance;
  }

  public static UserDao getInstance(MongoCollection<Document> collection){
    instance = new UserDao(collection);
    return instance;
  }

  @Override
  public void put(UserDto messageDto) {
    collection.insertOne(messageDto.toDocument());
  }

  public List<UserDto> query(Document filter){
    return collection.find(filter)
        .into(new ArrayList<>())
        .stream()
        .map(UserDto::fromDocument)
        .collect(Collectors.toList());
  }

  //New method needed for searching users
  public List<UserDto> searchUsers(String search) { // takes search input
    List<UserDto> users = collection.find(Filters.regex("userName", "^" + search))  // case sensitive search for userNames in document
            .into(new ArrayList<>())
            .stream()
            .map(UserDto::fromDocument) // converts document to UserDto object
            .collect(Collectors.toList());

    return users; // returns list of users from searchUsers
}

  //Deletes a user document from the database collection based on the specified query.
  //Added for the DeleteUserHandler
  public void deleteUser(Document query) {
    collection.deleteOne(query);
  }
}


import './App.css';
import React from 'react';
import Cookies from 'universal-cookie';


const cookies = new Cookies();

function App() {
  const [userName, setUserName] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [isLoading, setIsLoading] = React.useState(false);
  const [isLoggedIn, setIsLoggedIn] = React.useState(false);
  const [errorMessage, setErrorMessage] = React.useState('');

  // new state variables for chat box
  const [toId, setToId] = React.useState(['']);
  const [message, setMessage] = React.useState('');

  // new state variable for list of convos
  const [conversations, setConversations] = React.useState([]); // default empty array

  // new variables for displaying message history
  const [conversationId, setConversationId] = React.useState('');
  const [messageThread, setMessageThread] = React.useState([]);

  // new variables for searching users
  const [searchResults, setSearchResults] = React.useState([]);

  React.useEffect(() => {
    // this will run anytime conversationId changes
    getConversation();
  },[conversationId]);

  async function getConversation() {
    const httpSettings = {
        method: 'GET',
        headers: {
          auth: cookies.get('auth'), // utility to retrive cookie from cookies
        }
      };
      const result = await fetch('/getConversation?conversationId=' + conversationId, httpSettings);
      const apiRes = await result.json();
      console.log(apiRes);
      if (apiRes.status) {
        // worked
        console.log(apiRes.data);
        setMessageThread(apiRes.data); // java side should return list of all convo for this user
      } else {
        setErrorMessage(apiRes.message);
      }
    }

  async function getConversations() {
    const httpSettings = {
      method: 'GET',
      headers: {
        auth: cookies.get('auth'), // utility to retrive cookie from cookies
      }
    };
    const result = await fetch('/getConversations', httpSettings);
    const apiRes = await result.json();
    console.log(apiRes);
    if (apiRes.status) {
      // worked
      setConversations(apiRes.data); // java side should return list of all convos for this user
    } else {
      setErrorMessage(apiRes.message);
    }
  }

  async function handleSubmit() {
    setIsLoading(true);
    setErrorMessage(''); // fresh error message each time
    const body = {
      userName: userName,
      password: password,
    };
    const httpSettings = {
      body: JSON.stringify(body),
      method: 'POST'
    };
    const result = await fetch('/createUser', httpSettings);
    const apiRes = await result.json();
    console.log(apiRes);
    if (apiRes.status) {
      // user was created
      // todo
    } else {
      // some error message
      setErrorMessage(apiRes.message);
    }
    setIsLoading(false);
  };

  async function handleLogIn() {
    setIsLoading(true);
    setErrorMessage(''); // fresh error message each time
    const body = {
      userName: userName,
      password: password,
    };
    const httpSettings = {
      body: JSON.stringify(body),
      method: 'POST'
    };
    const result = await fetch('/login', httpSettings);
    if (result.status === 200) {
      // login worked
      setIsLoggedIn(true);
      getConversations();
    } else {
      // login did not work
      setErrorMessage(`Username or password incorrect.`);
    }

    setIsLoading(false);
  };


 // function to fetch search results
async function searchUsers(searchQuery) {
  if (searchQuery.length < 1) {
    setSearchResults([]); // checks if searchQuery is empty
    return;
  }

  const httpSettings = {
    method: 'GET',
    headers: {
      auth: cookies.get('auth'),
    },
  };

  const result = await fetch('/searchUsers?search=' + searchQuery, httpSettings); // searchQuery holds userName info

  if (result.ok) {
    // Check if the response is empty
    if (result.status === 204) {
      setSearchResults([]);
      return;
    }
    const apiRes = await result.json();
    if (apiRes.status) {
      setSearchResults(apiRes.data);
    } else {
      setErrorMessage(apiRes.message);
    }
  } else {
    console.error('Network response was not OK');
    setErrorMessage('Network response was not OK');
  }
}

  function handleUserSelect(user) {
    setToId(user.userId); // set the 'toId' state to the selected user's id
  }

  async function handleLogIn() {
    setIsLoading(true);
    setErrorMessage(''); // fresh error message each time
    const body = {
      userName: userName,
      password: password,
    };
    const httpSettings = {
      body: JSON.stringify(body),
      method: 'POST'
    };
    const result = await fetch('/login', httpSettings);
    if (result.status === 200) {
      // login worked
      setIsLoggedIn(true);
      getConversations();
    } else {
      // login did not work
      setErrorMessage(`Username or password incorrect.`);
    }

    setIsLoading(false);
  };

  async function handleSendMessage() {
    if(!isLoggedIn){
      return;
    }
    setIsLoading(true);
    setErrorMessage(''); // fresh error message each time
    const body = {
      fromId: userName,
      toId: toId,
      message: message,
    };
    const httpSettings = {
      body: JSON.stringify(body),
      method: 'POST',
      headers: {
        auth: cookies.get('auth'), // utility to retrive cookie from cookies
      }
    };
    const result = await fetch('/createMessage', httpSettings);
    const apiRes = await result.json();
    console.log(apiRes);
    if (apiRes.status) {
      // worked
      setMessage('');
      getConversations();
      setConversationId(apiRes.data[0].conversationId);
      getConversation();
    } else {
      setErrorMessage(apiRes.message);
    }
    setIsLoading(false);
  };

  async function addBox(){
    var element = document.querySelector(".boxes");
    var div = document.createElement("div");
    var inputBox = document.createElement("input");
    inputBox.setAttribute("type","text");
    inputBox.className = "input-box";

    div.append(inputBox);
    element.append(div);
  };

  async function removeBox() {
    var element = document.querySelector(".boxes");
    var box = element.lastChild;
    if (box) {
      element.removeChild(box);
    }
  };

  async function collectInputValues() {
    const inputBoxes = document.querySelectorAll('.input-box');
    const inputValues = Array.from(inputBoxes).map((box) => box.value);
    setToId(inputValues);
  }

  // When collectInputValues updates toId, it will perform handleSendMessage()
  // collectInputValues() should be called instead of handleSendMessage() else where in the code
  React.useEffect(() => {
      handleSendMessage();
    }, [toId]);

  if (isLoggedIn) {
    return (
<div className="App">
        <h1>Welcome {userName}</h1>
        <div> Search for Users:
          <input 
            placeholder="Search Users"
            onChange={e => searchUsers(e.target.value)}
          />
          {searchResults.length > 0 && (
          <div className={`user-list-box ${searchResults.length === 0 ? 'no-results' : 'with-results'}`}>
          {searchResults.map(user => (
          <p key={user.userId} className="user-item" onClick={() => handleUserSelect(user)}>
         {user.userName}
        </p>
      ))}
</div>
          )}

        </div>
        <div>
          To: <input
              // value = {toId}
              // onChange={e => setToId(e.target.value)} -----> I think these are unecessary but keeping just in case.
               type="text"
               className="input-box"/>

          <button onClick={addBox}>+</button>
          <button onClick={removeBox}>-</button>
        </div>
        <div className = "boxes"></div>

        <div className="chat-container">
          <div className="user-list">
            {conversations.map(conversation => (
              <div
                className="user-list-item"
                onClick={() => {
                  setConversationId(conversation.conversationId);
                  //setToId(conversation.otherUser); //What's the point of this??
                }}
              >
                Convo: {conversation.conversationId}
              </div>
            ))}
          </div>
          <div className="chat-content">
            <div className="chat-header">
              <h3>Selected Conversation: {conversationId}</h3>
            </div>
            <div className="message-box">
              {messageThread.map(messageDto => (
                <p className="message-text">
                  <strong>{messageDto.fromId}:</strong> {messageDto.message}
                </p>
              ))}
            </div>
            <div className="message-input">
              <textarea
                value={message}
                onChange={e => setMessage(e.target.value)}
              />
              <button onClick={collectInputValues}>Send Message</button>
            </div>
          </div>
        </div>
        <div>{errorMessage}</div>
      </div>
    );
  }


  return (
    <div className="App">
      <input value={userName} onChange={(e) => setUserName(e.target.value)} />
      <input
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        type="password"
      />
      <button onClick={handleSubmit} disabled={isLoading}>
        Register
      </button>
      <button onClick={handleLogIn} disabled={isLoading}>
        Log in
      </button>
      <div>{isLoading ? "Loading ..." : null}</div>
      <div>{errorMessage}</div>
    </div>
  );
}

export default App;
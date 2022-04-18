import java.io.*;
import java.net.*;
import java.util.ArrayList;

class Server {
	public static void main(String[] args) {
		ServerSocket server = null;
		try {
			// Load User file into ArrayList, save new Users to it ---> update the file
			server = new ServerSocket(1234);
			server.setReuseAddress(true);
	
			// Looping to accept an indefinite number of client connections
			while (true) {
				// Accept client connection
				Socket client = server.accept(); 
				System.out.println("IN SERVER: New client connected " + client.getInetAddress().getHostAddress());
	
				// Creating a new thread
				ClientHandler clientSock = new ClientHandler(client);
				
				// This thread will handle the client separately
				new Thread(clientSock).start();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (server != null) {
				try {
					server.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static class ClientHandler implements Runnable {
		private final Socket clientSocket;
		private User localUser;
		private Boolean authenticated = false;
		
		private static ArrayList<Socket> allUserSockets = new ArrayList<Socket>();
		private static ArrayList<ObjectOutputStream> allOutputStreams = new ArrayList<ObjectOutputStream>();
		private static ArrayList<User> allUsers = new ArrayList<User>();
	    private static ArrayList<ChatRoom> allChatRooms = new ArrayList<ChatRoom>();

		private static int userNum = 0;

		private String username;
		
		// Constructor
		public ClientHandler(Socket socket) {
			this.clientSocket = socket;
			System.out.println("New thread created");
			userNum++;
			System.out.println("Number of connect clients: " + userNum);
			username = "User" + userNum;
			allUserSockets.add(socket);
			System.out.println(username + " " + socket);

			if (userNum == 0) {
				loadUsers(allUsers);
			}

			username = "User " + userNum++;

			
		}
	
		public void run() {
			try {
			    // Allows us to receive Messages from the client
				InputStream inputStream = clientSocket.getInputStream();
				ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
				
				
				// Allows us to send Messages back to the client
				OutputStream outputStream = clientSocket.getOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
				allOutputStreams.add(objectOutputStream);

				Message loginMessage;
				loginMessage = (Message) objectInputStream.readObject();


				String[] values = loginMessage.getText().split("/");

				// Attempting to authenticate based on the first login attempt
				for (int i = 0; i < allUsers.size(); i++) {
					if (allUsers.get(i).getName.equals(values[0])) {
						if (allUsers.get(i).getPassword().equals(values[1])) {
							localUser = allUsers.get(i);
							localUser.setObjectOutputStream(objectOutputStream);
							authenticated = true;

							loginMessage.setStatus("VERIFIED");
							objectOutputStream.writeObject(loginMessage);
						}
						else {
							loginMessage.setStatus("FAILED");
							loginMessage.setText("Invalid password");
							objectOutputStream.writeObject(loginMessage);
							break;
						}
					}
					else {
						loginMessage.setStatus("FAILED");
						loginMessage.setText("User not found.");
						objectOutputStream.writeObject(loginMessage);
					}
				}
				
				// **** USER AUTHENTICATION, CREATE THE USER OBJECT AND SET ITS ATTRIBUTES ****
				while (!authenticated) {
					loginMessage = (Message) objectInputStream.readObject();

					for (int i = 0; i < allUsers.size(); i++) {
						if (allUsers.get(i).getName.equals(values[0])) {
							if (allUsers.get(i).getPassword().equals(values[1])) {
								localUser = allUsers.get(i);
								localUser.setObjectOutputStream(objectOutputStream);
								authenticated = true;
	
								loginMessage.setStatus("VERIFIED");
								objectOutputStream.writeObject(loginMessage);
							}
							else {
								loginMessage.setStatus("FAILED");
								loginMessage.setText("Invalid password");
								objectOutputStream.writeObject(loginMessage);
								break;
							}
						}
						else {
							loginMessage.setStatus("FAILED");
							loginMessage.setText("User not found.");
							objectOutputStream.writeObject(loginMessage);
						}
					}
			    }
				
				// 
		
				// Reading the message from the client thread
				try {
                    Message messageFromClient;
					// This is where the Server takes Messages from the Clients and decides what to do based on the Message's type
					while (true) {
						messageFromClient = (Message) objectInputStream.readObject();
						
						switch (messageFromClient.getType()) {
			            case "CHATROOM": /* 
			            				    for (int i = 0; i < allChatRooms.size(); i++) {
			            	                    if (allChatRooms.get(i).getRoomName().equals(localUser.getActiveChatRoom()) {
			            						    messageFromClient.setText(localUser.getName() + ": " + messageFromClient.getText());
			            	                        allChatRooms.get(i).sendMessage(messageFromClient);
			            							Message sendReceipt = new Message(status = VERIFIED);
			            							objectOutputStream.writeObject(sendReceipt);
			            	             
			            	             
			            					*/
			            					System.out.println("TYPE: CHATROOM");
			            					break;
			            					
			            case "DISPLAYCHATROOMS": /*
			            						   String[] roomNames = new String[allChatRooms.size()]
			            	                       for (int i = 0; i < allChatRooms.size(); i++) {
			            	                           roomNames[i] = allChatRooms.get(i).getRoomName();
			            	                       }
			            	                       MessageFromUser.setRoomList(roomNames)
			            	                       objectOutputStream.writeObject(messageFromClient);
			            	                       
			            	                      
			            	                    */
			            						  System.out.println("TYPE: DISPLAYCHATROOM");
		                     				      break;
			            case "JOINCHATROOM": /*
			            					 for (int i = 0; i < allChatRooms.size(); i++) {
  	                                              if (allChatRooms.get(i).getRoomName().equals(messageFromClient.getText())) {
  	                                                  localUser.setActiveChannel(messageFromClient.getText());
			            					          allChatRooms.get(i).addUser(localUser);
			            				     		  messageFromClient.setStatus("VERIFIED");
			            				     		  objectOutputStream.writeObject(messageFromClient);
			            				     		  
			            					*/
			            	                System.out.println("TYPE: JOINCHATROOM");
		                     				break;
			            case "CREATECHATROOM":  /*
			            						  String roomName = messageFromClient.getText();
			            						  for (int i = 0; i < allChatRooms.size(); i++) {
  	                                                  if (allChatRooms.get(i).getRoomName().equals(messageFromClient.getText())) {
  	                                                      messageFromClient.setStatus("FAILED");    
  	                                                      return;               
  	                                                  }
  	                                              // If we are here, the name is free to be used
                        
			            						  allChatRooms.add(new ChatRoom(localUser, messageFromClient);
			            						  Message sendReceipt = new Message(status = VERIFIED);
			            						  objectOutputStream.writeObject(sendReceipt);
			            						  
			            
			            */
			            	 System.out.println("TYPE: CREATECHATROOM");
		                     break;
			            case "CHANGEPASSWORD":  // localUser.changePassword(messageFromClient.getText());
			            		                // Probably want to send confirmation back
			            	 System.out.println("TYPE: CHANGEPASSWORD");
		                     break;
			            case "CREATEUSER":  /*
			            				   if (!localUser instanceof Supervisor) {
			            				       messageFromClient.setStatus("FAILED");
			                                   messageFromClient.setText("You are not a Supervisor.");
			                                   objectOutputStream.writeObject(messageFromClient);
			                                   
			                                   return;
			            				   }
			            				   
			                               for (int i = 0; i < allUsers.size(); i++) {
			                                   if (allUsers.getName().equals(the name from the message)) {
			                                       messageFromClient.setStatus("FAILED");
			                                       messageFromClient.setText("This User already exists.");
			                                       objectOutputStream.writeObject(messageFromClient);
			                                       return;
			                                   }
			                               }
			                               
			                               // The User does not already exist and can be added
			                               allUsers.add(new User(name, password, null, null); // They do not yet have an output stream or active chat room
			                               messageFromClient.setStatus("VERIFIED");
			                               objectOutputStream.writeObject(messageFromClient);
			                               
			                               // Write the User to the file now
										   saveNewUser( CONTENT OF THE NEW USER)

			                               return;
			                               
			            
			           
			            */
			            	 System.out.println("TYPE: CREATEUSER");
		                     break;
			            case "CREATESUPERVISOR":  /*
			            					   if (!localUser instanceOf Supervisor) {
			            				           messageFromClient.setStatus("FAILED");
			                                       messageFromClient.setText("You are not a Supervisor.");
			                                       objectOutputStream.writeObject(messageFromClient);
			                                       
			                                       return;
			            				       }
			            				       
			            				       for (int i = 0; i < allUsers.size(); i++) {
			                                       if (allUsers.get(i).getName().equals(the name from the message)) {
			                                           messageFromClient.setStatus("FAILED");
			                                           messageFromClient.setText("This User already exists.");
			                                           objectOutputStream.writeObject(messageFromClient);
			                                             
			                                           return;
			                                       }
			                                   }
			            
			            					// The User does not already exist and can be added
											User newUser = new UserUser(name, password, null, null);
			                                allUsers.add(newUser); // They do not yet have an output stream or active chat room
			                                messageFromClient.setStatus("VERIFIED");
			                                objectOutputStream.writeObject(messageFromClient);
			            
			            				
			            				  // Should we write this User to the file now?
										  saveUser(newUser);
			            */
			            	 System.out.println("TYPE: CREATESUPERVISOR");
		                     break;
		                     
			            case "DISPLAYUSERS":  /*
			            					   String listOfUsers = "";
			            				       for (int i = 0; i < allUsers.size(); i++) {
			                                       listOfUsers += allUsers.get(i).getName() + "\n";
			                                   }
			                                   
			                                   messageFromClient.setText(listOfUsers);
			                                   objectOutputStream.writeObject(messageFromClient);
			            
			            
			            
			            
			            */
			            	 System.out.println("TYPE: DISPLAYUSERS");
		                     break;
			            case "REMOVECHATUSER":  /*
			            				         String userToRemove = messageFromClient.getText();
			            						  for (int i = 0; i < allChatRooms.size(); i++) {
  	                                                  if (allChatRooms.get(i).getRoomName().equals(localUser.getActiveChatRoom())) {
  	                                                        allChatRooms.get(i).removeUser(localUser, messageFromClient);  
  	                                                        return;
  	                                                  }
			            					   
				            
					            */
								System.out.println("TYPE: REMOVECHATUSER");						
								break;
			            case "LOCKCHAT":  /*
   				                            String userToRemove = messageFromClient.getText();
   						  				    for (int i = 0; i < allChatRooms.size(); i++) {
                                   	            if (allChatRooms.get(i).getRoomName().equals(localUser.getActiveChatRoom())) {
                                                    allChatRooms.get(i).setChatLock(localUser, messageFromClient);  
                                                }
                                           }
   					   
       
           */
		  					System.out.println("TYPE: LOCKCHAT");
			            	break;
			            case "UNLOCKCHAT":  /*
					   				         String userToRemove = messageFromClient.getText();
					   						  for (int i = 0; i < allChatRooms.size(); i++) {
					                              if (allChatRooms.get(i).getRoomName().equals(localUser.getActiveChatRoom())) {
					                                  allChatRooms.get(i).setChatUnlock(localUser, messageFromClient);  
					                              }
					                          }
   					   
       
           */
			            case "RETRIEVELOGS ":  /*
			            
			            */

						System.out.println("TYPE: RETRIEVELOGS");
		                     break;
			            case "DELETEUSER": /*
			            				     if (!localUser instanceOf Supervisor) {
			            				           messageFromClient.setStatus("FAILED");
			                                       messageFromClient.setText("You are not a Supervisor.");
			                                       return;
			            				       }
			            				       
			            				       for (int i = 0; i < allUsers.size(); i++) {
			                                       if (allUsers.get(i).getName().equals(the name from the message)) {
			                                       
			                                           // This is shitty and will break chat rooms I think, they would have to start checking for null objects
			                                       	   allUsers.get(i) = null; // Removes all references to the User, deletes their object
			                                       	   
			                                           messageFromClient.setStatus("VERIFIED");
			                                           messageFromClient.setText("The User has been deleted.");
			                                           return;
			                                       }
			                                   }
			            
			            
			            
			            */
						System.out.println("TYPE: DELETEUSER");
		                     break;
			            default: // ???????
						System.out.println("TYPE: DEFAULT");
			                     break;
			        
						}
					}
				}
				catch (Exception e) {
					System.out.println("Error");
				}
			}
			catch (Exception e) {
				System.out.println("There was an exception");
			}
		}

		public void saveUser(User newUser) {
			FileWriter writer = new FileWriter("src\\fileio\\users", true);
			
			writer.write("\nU/");
			writer.write( newUser.getName() + "/");
			writer.write(newUser.getPassword());
			
			writer.close();
			return;
		}

		public void loadUsers(ArrayList<User> allUsers) {
			try {
				BufferedReader reader;
				User newUser;

				reader = new BufferedReader(new FileReader("src\\fileio\\users"));
				String line = reader.readLine();
				
				String[] values = line.split("/");
				if (values[0].equals("S")) {
					newUser = new Supervisor(values[1], values[2]);
				}
				else {
					newUser = new User(values[1], values[2]);
				}

				line = reader.readLine();
			
				while (line != null) {	
					values = line.split("/");
					if (values[0].equals("S")) {
						newUser = new Supervisor(values[1], values[2]);
					}
					else {
						newUser = new User(values[1], values[2]);
					}
	
					line = reader.readLine();
				}
					reader.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			return;
		}

		public void saveMessage() {
			
		
	
		}
	}
}

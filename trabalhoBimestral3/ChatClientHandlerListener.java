package trabalhoBimestral3;

public interface ChatClientHandlerListener {
	
	void handleLogin(String username);
	void handleMessage(String username, String message);
        void pingPong(String username);
        void finalizado(String username);
}

import java.awt.BorderLayout;
import java.awt.Container;
import java.net.MalformedURLException;

import javax.swing.JFrame;

public class App extends JFrame{
	public App() {
		setTitle("몸으로 말해요 게임");
		setSize(900,700);//사이즈설정
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//x버튼을 눌렀을 때, 끝
		
		Container c=getContentPane();
		c.setLayout(new BorderLayout());//BorderLayout로 설정한다.
		try {
			c.add(new StartPanel(this),BorderLayout.CENTER);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//IntroPanel로 패널을 보여줘서, 게임의 첫번째 장면을 보여준다.
		
		setResizable(false);//사이즈를 조절하지 못하도록한다.
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new App();
		new App();
		new App();
		new App();
	}

}

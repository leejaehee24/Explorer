import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

public class MainFrame extends JFrame {

	static final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	static final int screen_x = (int)(screen.getWidth()-100);
	static final int screen_y = (int)(screen.getHeight()-100);
	private SubFrame subframe; // 구체적 패널
	private JPanel icon;  // 아이콘
	private JMenuBar menu; // 메뉴바

	MainFrame()
	{
		super("Explorer");
		init();
	}

	void init(){
		setSize(screen_x, screen_y);
		setLayout(null); // 레이아웃 default로 지정, setbounds() 이용하여, 컴포넌트 배치하기 위해서

		subframe = new SubFrame(this); // 서브프레임 인스턴트 생성
		icon = new Icon(subframe); // 아이콘 패널 생성할때, subframe의 객체를 넘겨줌
		menu = new Menu(subframe); // 메뉴 생성할때, subframe의 객체를 넘겨줌

		run(menu, icon, subframe);

		int xpos = (int)(screen.getWidth()/2)-(super.getWidth()/2);
		int ypos = (int)(screen.getHeight()/2)-(super.getHeight()/2);
		super.setLocation(xpos,ypos);

		this.setResizable(false); // 크기 변경 하지 못하게
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
	
	void run(JMenuBar menu, JPanel icon, JPanel subFrame){
		setJMenuBar(menu); // 메뉴바 생성
		add(icon); // 아이콘 붙임
		add(subframe); // 구체적 패널 붙임
	}

	void run(JMenuBar menu){
		setJMenuBar(menu); // 메뉴바 생성
		validate();
	}
	
	public static void main(String[] args)
	{
		new MainFrame();
	}


}

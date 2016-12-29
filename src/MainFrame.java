import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

public class MainFrame extends JFrame {

	static final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	static final int screen_x = (int)(screen.getWidth()-100);
	static final int screen_y = (int)(screen.getHeight()-100);
	private SubFrame subframe; // ��ü�� �г�
	private JPanel icon;  // ������
	private JMenuBar menu; // �޴���

	MainFrame()
	{
		super("Explorer");
		init();
	}

	void init(){
		setSize(screen_x, screen_y);
		setLayout(null); // ���̾ƿ� default�� ����, setbounds() �̿��Ͽ�, ������Ʈ ��ġ�ϱ� ���ؼ�

		subframe = new SubFrame(this); // ���������� �ν���Ʈ ����
		icon = new Icon(subframe); // ������ �г� �����Ҷ�, subframe�� ��ü�� �Ѱ���
		menu = new Menu(subframe); // �޴� �����Ҷ�, subframe�� ��ü�� �Ѱ���

		run(menu, icon, subframe);

		int xpos = (int)(screen.getWidth()/2)-(super.getWidth()/2);
		int ypos = (int)(screen.getHeight()/2)-(super.getHeight()/2);
		super.setLocation(xpos,ypos);

		this.setResizable(false); // ũ�� ���� ���� ���ϰ�
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
	
	void run(JMenuBar menu, JPanel icon, JPanel subFrame){
		setJMenuBar(menu); // �޴��� ����
		add(icon); // ������ ����
		add(subframe); // ��ü�� �г� ����
	}

	void run(JMenuBar menu){
		setJMenuBar(menu); // �޴��� ����
		validate();
	}
	
	public static void main(String[] args)
	{
		new MainFrame();
	}


}

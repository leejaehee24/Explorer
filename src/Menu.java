import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;


public class Menu extends JMenuBar{
	private JMenuBar bar;
	private JMenu helpmenu, filemenu, viewmenu, historymenu;
	private JMenuItem newfile, openfile, savefile, close, help, maker, big, normal, table;
	private SubFrame subframe;
	private Vector<String> vector;
	private JMenuItem[] history_item;

	Menu(SubFrame subframe){
		this.subframe = subframe;
		init();

	}

	Menu(SubFrame subframe, Vector v){
		this.subframe = subframe;
		this.vector = v;
		init();

	}

	Menu(Vector v){
		this.subframe = subframe;
		this.vector = v;
		init();

	}

	void init(){
		filemenu = new JMenu("파일(F)");
		filemenu.setMnemonic('F');
		openfile = new JMenuItem("열기(O)");
		openfile.setMnemonic('O');
		openfile.setAccelerator(KeyStroke.getKeyStroke('O',Event.CTRL_MASK)); 
		openfile.addActionListener(new MyActionListener());
		filemenu.add(openfile);

		savefile = new JMenuItem("저장(S)");
		savefile.addActionListener(new MyActionListener());
		savefile.setMnemonic('S');
		savefile.setAccelerator(KeyStroke.getKeyStroke('S',Event.CTRL_MASK)); 
		filemenu.add(savefile);

		close = new JMenuItem("닫기");
		close.setMnemonic('Q');
		close.setMnemonic('Q');
		close.setAccelerator(KeyStroke.getKeyStroke('Q',Event.CTRL_MASK)); 
		close.addActionListener(new MyActionListener());
		filemenu.add(close);

//		//보기 메뉴 만들기
//		viewmenu = new JMenu("보기");
//		big = new JMenuItem("큰 아이콘");
//		big.addActionListener(new MyActionListener());
//		viewmenu.add(big);
//		normal = new JMenuItem("보통 아이콘");
//		normal.addActionListener(new MyActionListener());
//		viewmenu.add(normal);
//		table = new JMenuItem("자세히");
//		table.addActionListener(new MyActionListener());
//		viewmenu.add(table);

		//최근 파일 History
		historymenu = new JMenu("최근파일");
		vector = subframe.getHistory();
		history_item = new JMenuItem[5];
		for(int i = 0 ; i < vector.size() ; i++){
			String s = vector.get(i);
			history_item[i] = new JMenuItem(s.substring(s.lastIndexOf("/") + 1));
			history_item[i].addActionListener(new MyActionListener());
			historymenu.add(history_item[i]);
		}
		
		//도움말 메뉴 만들기
		helpmenu = new JMenu("도움말(D)");
		helpmenu.setMnemonic('D');

		maker = new JMenuItem("제작자(M)");
		maker.setMnemonic('M');
		maker.setAccelerator(KeyStroke.getKeyStroke('M',Event.CTRL_MASK)); 
		maker.addActionListener(new MyActionListener());
		helpmenu.add(maker);

		help = new JMenuItem("Help(H)");
		help.setMnemonic('H');
		help.setAccelerator(KeyStroke.getKeyStroke('H',Event.CTRL_MASK)); 
		help.addActionListener(new MyActionListener());
		helpmenu.add(help);

		
		
		add(filemenu);
//		add(viewmenu);
		add(historymenu);
		add(helpmenu);
		validate();
	}


	private class MyActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			Object obj = e.getSource();
			if(obj == savefile){
				try{
					String path = subframe.getLast_path();
					System.out.println(path);
					subframe.saveFile(path);
				}
				catch(Exception e1){
					System.out.println(e1.getMessage());
				}
			}
			else if (obj == history_item[0]){
				String temp = vector.get(0).substring(vector.get(0).lastIndexOf("/") + 1)+ "   (" + vector.get(0) + ")";
				subframe.setInfo(temp);
				subframe.openFile(vector.get(0));
				subframe.setLast_path(vector.get(0));
			}
			else if (obj == history_item[1]){
				String temp = vector.get(1).substring(vector.get(1).lastIndexOf("/") + 1)+ "   (" + vector.get(1) + ")";
				subframe.setInfo(temp);
				subframe.openFile(vector.get(1));
				subframe.setLast_path(vector.get(1));
			}
			else if (obj == history_item[2]){
				String temp = vector.get(2).substring(vector.get(2).lastIndexOf("/") + 1)+ "   (" + vector.get(2) + ")";
				subframe.setInfo(temp);
				subframe.openFile(vector.get(2));
				subframe.setLast_path(vector.get(2));
			}
			else if (obj == history_item[3]){
				String temp = vector.get(3).substring(vector.get(3).lastIndexOf("/") + 1)+ "   (" + vector.get(3) + ")";
				subframe.setInfo(temp);
				subframe.openFile(vector.get(3));
				subframe.setLast_path(vector.get(3));
			}
			else if (obj == history_item[4]){
				String temp = vector.get(4).substring(vector.get(4).lastIndexOf("/") + 1)+ "   (" + vector.get(4) + ")";
				subframe.setInfo(temp);
				subframe.openFile(vector.get(4));
				subframe.setLast_path(vector.get(4));
			}

			//패널 크기 메뉴
			else if (obj == big){
				System.out.println("아이콘 크게 ");
			}
			else if (obj == normal){
				System.out.println("아이콘 보통");
			}
			else if (obj == table){
				System.out.println("테이블로 보기 ");
			}
			else if(obj == openfile)
			{
				JFileChooser fileChooser = new JFileChooser();
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					subframe.openFile(selectedFile.getAbsolutePath());
				}
			}
			else if(obj == close){
				System.exit(0);
			}
			else if(obj == help){
				JOptionPane.showMessageDialog(null, "설명서 별도 첨부");
			}
			else if(obj == maker){
				JOptionPane.showMessageDialog(null, "컴퓨터학부 이재희\n"+"컴퓨터학부 정동원");
			}
		}
	}
}

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.BoundedRangeModel;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Icon extends JPanel{
	private SubFrame subframe;
	private JSlider slider; // 패널정하기
	private int panel_state;
	private JRadioButton rb1, rb2, rb3;
	private JTextField size1, size2, type, year; 
	private int search_state = 0;
	private JTextField edit, edit2, edit3;

	Icon(SubFrame subframe){
		this.subframe = subframe;

		setLayout(null);
		setBounds(0,0,MainFrame.screen_x, MainFrame.screen_y / 20); // 패널 크기

		//save 아이콘 만들기
		ImageIcon save = new ImageIcon("save.png");
		JButton save_btn = new JButton(save);
		save_btn.setBorderPainted(false); // 버튼 테두리없애기
		save_btn.setMargin(new Insets(0, 0, 0, 0)); // 여백 없애기
		save_btn.setContentAreaFilled(false);
		save_btn.setBounds(8,5,40,40);
		save_btn.setFocusPainted(false);
		save_btn.setToolTipText("save");
		save_btn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				subframe.saveFile(subframe.getLast_path());
			}

		});
		//		save_btn.setSize(new Dimension(save.getIconWidth(),save.getIconHeight()));
		add(save_btn);
		//open 아이콘 만들기

		ImageIcon open = new ImageIcon("open.png");
		JButton open_btn = new JButton(open);
		open_btn.setBorderPainted(false); // 버튼 테두리없애기
		open_btn.setMargin(new Insets(0, 0, 0, 0)); // 여백 없애기
		open_btn.setContentAreaFilled(false);
		open_btn.setBounds(55,5,40,40);
		open_btn.setToolTipText("Open");
		open_btn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFileChooser fileChooser = new JFileChooser();
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					subframe.openFile(selectedFile.getAbsolutePath());
					subframe.setLast_path(selectedFile.getAbsolutePath());
				}
			}

		});
		//		save_btn.setSize(new Dimension(save.getIconWidth(),save.getIconHeight()));
		add(open_btn);

		//serch 부분

		ImageIcon search = new ImageIcon("search01.png");
		JButton search_btn = new JButton(search);
		search_btn.setBorderPainted(false); // 버튼 테두리없애기
		search_btn.setMargin(new Insets(0, 0, 0, 0)); // 여백 없애기
		search_btn.setContentAreaFilled(false);
		search_btn.setBounds(102,5,40,40);
		search_btn.setToolTipText("Search");
		search_btn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JDialog dialog = new JDialog();
				dialog.setTitle("파일 찾기");
				dialog.setSize(400,270);
				dialog.setLayout(null);
				search_state = 0;
				edit = new JTextField();
				edit.setBounds(30, 10, 200, 35);

				JButton find = new JButton("찾기");
				find.setBounds(250, 10, 100, 35);
				find.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
//						try{
							String s = edit.getText();
//							if(s.length() <= 1){
//								throw new myException("한글자 이상 입력하세요");
//							}							

							if(search_state == 0){
								// 기본 검색옵션
								subframe.Sort_Table(s);
							}


							JOptionPane.showMessageDialog(null, "완료되었습니다.");
							dialog.dispose();
//						}catch(myException e1){
//							JOptionPane.showMessageDialog(null, e1.getMessage());
//						}

					}

				});

				JButton btn2 = new JButton("옵션 검색");
				btn2.setBounds(250, 170, 100,30);
				btn2.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						try{
							if(search_state == 0){
								throw new myException("라디오버튼중 하나를 선택하세요");
							}
							if(search_state == 1 && (size1.getText().length() == 0 || 
									size2.getText().length() == 0)){
								throw new myException("파일 크기를 입력해주세요.");
							}
							if(search_state == 2 && (type.getText().length() == 0)){
								throw new myException("파일 타입을 입력해주세요.");
							}
							if(search_state == 3 && year.getText().length() == 0){
								throw new myException("년도를 입력해주세요.");
							}

							if(search_state == 1){
								//파일 크기 옵션
								String f_size1 = size1.getText();
								String f_size2 = size2.getText();
								subframe.Sort_Table(f_size1, f_size2,search_state);
							}
							else if(search_state == 2){
								// 파일 타입 옵션
								String f_type = type.getText();
								subframe.Sort_Table(f_type, "", search_state);

							}
							else if(search_state == 3){
								// 년도 옵션
								String f_year = year.getText();
								subframe.Sort_Table(f_year, "",  search_state);
							}
						}catch(myException e2){
							JOptionPane.showMessageDialog(null, e2.getMessage());
						}
					}

				});
				dialog.add(btn2);
				// 옵션 라디오 버튼
				JLabel option = new JLabel("<<  Option  >>");
				option.setBounds(150, 50, 100, 40);
				dialog.add(option);
				JRadioButton rb1 = new JRadioButton("  파 일 크  기  :");
				JRadioButton rb2 = new JRadioButton("  파일 확장자 :");
				JRadioButton rb3 = new JRadioButton("  수 정 날  짜  :");
				rb1.setBounds(30, 90, 105, 30);
				rb2.setBounds(30, 130, 105, 30);
				rb3.setBounds(30, 170, 105, 30);
				ButtonGroup group = new ButtonGroup();
				group.add(rb1);
				group.add(rb2);
				group.add(rb3);
				rb1.addItemListener(new MyItemListener());
				rb2.addItemListener(new MyItemListener());
				rb3.addItemListener(new MyItemListener());
				dialog.add(rb1);
				dialog.add(rb2);
				dialog.add(rb3);

				size1 = new JTextField(20);
				size2 = new JTextField(20);
				size1.setBounds(140, 90, 80, 30);
				JLabel label2 = new JLabel("~");
				label2.setBounds(230, 90, 10, 30);
				size2.setBounds(250, 90, 80, 30);
				JLabel label3 = new JLabel("bytes");
				label3.setBounds(340, 90, 50, 30);
				dialog.add(label2);
				dialog.add(size1);
				dialog.add(size2);
				dialog.add(label3);

				type = new JTextField(20);
				type.setBounds(140, 130, 80, 30);
				dialog.add(type);

				year = new JTextField(20);
				year.setBounds(140, 170, 80, 30);
				dialog.add(year);

				dialog.add(find);
				dialog.add(edit);
				int xpos = (int)(MainFrame.screen.getWidth()/2)-(dialog.getWidth()/2);;
				int ypos = (int)(MainFrame.screen.getHeight()/2)-(dialog.getHeight()/2);
				dialog.setLocation(xpos,ypos);
				dialog.setVisible(true);


			}

		});
		//		save_btn.setSize(new Dimension(save.getIconWidth(),save.getIconHeight()));
		add(search_btn);


		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 2, 0);

		slider.setBounds(MainFrame.screen_x-300, 3, 250, 50);
		slider.setPaintLabels(true);
		Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
		table.put(0, new JLabel("자세히"));
		table.put(1, new JLabel("작은 아이콘"));
		table.put(2, new JLabel("큰 아이콘"));

		slider.setLabelTable(table);
		slider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent changeEvent) {
				Object source = changeEvent.getSource();
				if (source instanceof BoundedRangeModel) {
					BoundedRangeModel aModel = (BoundedRangeModel) source;
					if (!aModel.getValueIsAdjusting()) {
						//						System.out.println("Changed: " + aModel.getValue());
					}
				} else if (source instanceof JSlider) {
					JSlider theJSlider = (JSlider) source;
					if (!theJSlider.getValueIsAdjusting()) {
						//						System.out.println("Slider changed: " + theJSlider.getValue());
						subframe.set_panel_state(theJSlider.getValue());
						subframe.Print_RHS();
					}
				} else {
					//					System.out.println("Something changed: " + source);
				}
			}

		});
		add(slider);

		ImageIcon folder = new ImageIcon("new_folder.png");
		JButton newDir = new JButton(folder);
		newDir.setBorderPainted(false); // 버튼 테두리없애기
		newDir.setMargin(new Insets(0, 0, 0, 0)); // 여백 없애기
		newDir.setContentAreaFilled(false);
		newDir.setBounds(150,5,40,40);
		newDir.setToolTipText("새폴더");
		
		newDir.setBounds(150,5,40,40);
		newDir.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JDialog dialog = new JDialog();
				dialog.setTitle("새폴더 만들기");
				dialog.setSize(310,100);
				dialog.setLayout(null);
				edit2 = new JTextField(150);
				edit2.setBounds(10, 10, 150, 35);
				
				JButton make = new JButton("만들기");
				make.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						try{
							if(edit2.getText().length() == 0){
								throw new myException("폴더명을 입력해주세요");
							}
							subframe.newDir(edit2.getText(), slider.getValue());
							JOptionPane.showMessageDialog(null, "폴더를 생성하였습니다.");
							dialog.dispose();
						}catch(myException e3){
							JOptionPane.showMessageDialog(null, e3.getMessage());
						}
					}
					
				});
				make.setBounds(180, 10, 100, 35);
				dialog.add(edit2);
				dialog.add(make);
				int xpos = (int)(MainFrame.screen.getWidth()/2)-(dialog.getWidth()/2);;
				int ypos = (int)(MainFrame.screen.getHeight()/2)-(dialog.getHeight()/2);
				dialog.setLocation(xpos,ypos);
				dialog.setVisible(true);
			}
			
		});
		add(newDir);

		ImageIcon newfileicon = new ImageIcon("new_file.png");
		JButton newFile = new JButton(newfileicon);
		newFile.setBounds(200,5,40,40);
		newFile.setBorderPainted(false); // 버튼 테두리없애기
		newFile.setMargin(new Insets(0, 0, 0, 0)); // 여백 없애기
		newFile.setContentAreaFilled(false);
		newFile.setToolTipText("새파일");
		newFile.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JDialog dialog = new JDialog();
				dialog.setTitle("새파일 만들기");
				dialog.setSize(310,100);
				dialog.setLayout(null);
				edit3 = new JTextField(150);
				edit3.setBounds(10, 10, 150, 35);
				
				JButton make = new JButton("만들기");
				make.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						try{
							if(edit3.getText().length() == 0){
								throw new myException("파일명을 입력해주세요");
							}
							subframe.newFile(edit3.getText(), slider.getValue());
							JOptionPane.showMessageDialog(null, "파일을 생성하였습니다.");
							dialog.dispose();
						}catch(myException e3){
							JOptionPane.showMessageDialog(null, e3.getMessage());
						}
					}
					
				});
				make.setBounds(180, 10, 100, 35);
				dialog.add(edit3);
				dialog.add(make);
				int xpos = (int)(MainFrame.screen.getWidth()/2)-(dialog.getWidth()/2);;
				int ypos = (int)(MainFrame.screen.getHeight()/2)-(dialog.getHeight()/2);
				dialog.setLocation(xpos,ypos);
				dialog.setVisible(true);
			}
			
		});
		add(newFile);
	}

	private class MyItemListener implements ItemListener{

		@Override
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			AbstractButton sel = (AbstractButton)e.getItemSelectable();
			if(e.getStateChange()==ItemEvent.SELECTED)
			{
				if(sel.getText().equals("  파 일 크  기  :")){
					search_state = 1;
				}
				else if(sel.getText().equals("  파일 확장자 :")){
					search_state = 2;
				}
				else if(sel.getText().equals("  수 정 날  짜  :")){
					search_state = 3;
				}
				else{
					search_state = 0;
				}
			}
		}
	}

	class myException extends Exception{
		myException(String s){
			super(s);
		}
	}
}
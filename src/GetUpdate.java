import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class GetUpdate extends JFrame implements ActionListener{
	private JButton button;
	private JTextField jTextField;
	private String profilepath = "src/updateTime.properties";
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private List<String> fileNameArr = new ArrayList<String>();
	private File file;
	private String time;
	private Properties properties = new Properties();
	public GetUpdate(){
		try {
			File proFile=new File(profilepath);
			properties.load(new FileReader(proFile.getAbsoluteFile()));
			this.time = properties.getProperty("time");
		} catch (Exception e) {
			e.printStackTrace();
		}
		button= new JButton("选择文件");
		jTextField = new JTextField(this.time);
		button.addActionListener(this);
		this.add(jTextField);
		this.setLayout(new FlowLayout());
		this.add(button);
		this.setTitle("更新文件导出");
		this.setBounds(200, 100, 800, 600);
		this.setVisible(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		new GetUpdate();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==button){//保存日志---------------
			this.file = this.getSaveFile();
			if(this.file != null)
				getUpdate(file);
		}
	}
	
	public File getSaveFile(){//选择存储地址并返回---------
		final JFileChooser jfc=new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//设置目录选择方式
		jfc.setCurrentDirectory(new File(this.properties.getProperty("projectPath")));//设置默认目录
		final int flag=jfc.showSaveDialog(null);
		if(flag==JFileChooser.APPROVE_OPTION)
			return jfc.getSelectedFile();	
		return null;
	}

	public void getUpdate(File file){
		Date time = null;
		try {
			time = sdf.parse(this.jTextField.getText());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		File src = new File(file.getAbsoluteFile()+"\\src");
		File classes = new File(file.getAbsoluteFile()+"\\web\\WEB-INF\\classes");
		File web = new File(file.getAbsoluteFile()+"\\web");
		doJavaFile(src,time);
		doClassFile(classes,time);
		doOtherFile(web,time);
		String newUpdate = sdf.format(new Date());
		this.writeProperties(properties,"time", newUpdate);
		this.jTextField.setText(newUpdate);
	}
	
	public void doJavaFile(File file,Date time){
		File[] fileArr = file.listFiles();
		if(fileArr !=null && fileArr.length>0){
			for(File f : fileArr){
				if(f.isDirectory()){
					doJavaFile(f,time);
				}else{
					Date last = new Date(f.lastModified());
					if(last.after(time)){
//						String[] strArr = f.getName().split("\\.");
//						fileNameArr.add(strArr[0]);
						fileNameArr.add(f.getName());
					}
				}
			}
		}
	}
	
	public void doClassFile(File file,Date time){
		File[] fileArr = file.listFiles();
		if(fileArr !=null && fileArr.length>0){
			for(File f : fileArr){
				if(f.isDirectory()){
					doClassFile(f,time);
				}else{
					for(String fileName : fileNameArr){
						if(fileName.endsWith(".java") && (f.getName().endsWith(fileName.split("\\.")[0]+".class") || f.getName().contains(fileName+"$"))){
							String result = f.getAbsolutePath().substring(this.file.getAbsolutePath().lastIndexOf("\\"));
							System.out.println(result);
						}else{
							
						}
					}
				}
			}
		}
	}
	
	public void doOtherFile(File file,Date time){
		File[] fileArr = file.listFiles();
		if(fileArr !=null && fileArr.length>0){
			for(File f : fileArr){
				if(f.isDirectory()){
					if(!"classes".equals(f.getName())){
						doOtherFile(f,time);
					}
				}else{
					Date last = new Date(f.lastModified());
					if(last.after(time)){
						String result = f.getAbsolutePath().substring(this.file.getAbsolutePath().lastIndexOf("\\"));
						System.out.println(result);
					}
				}
			}
		}
	}
	public void writeProperties(Properties props,String keyname,String keyvalue) {          
        try {   
            OutputStream fos = new FileOutputStream(profilepath);   
            props.setProperty(keyname, keyvalue);   
            props.store(fos, "Update '" + keyname + "' value");   
        } catch (IOException e) {   
            System.err.println("属性文件更新错误");   
        }   
	}  
}

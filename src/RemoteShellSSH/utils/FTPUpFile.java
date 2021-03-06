package RemoteShellSSH.utils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.*;

/**
 * @author bockey
 */
public class FTPUpFile {


    public static void FTPUpFile(Session session, String startPath, String targetPath) {

        Channel channel = null;
        try {
            channel = (Channel) session.openChannel("sftp");
            channel.connect(10000000);

            ChannelSftp sftp = (ChannelSftp) channel;
            try {
                //上传
                sftp.cd(targetPath);
//                Scanner scanner = new Scanner(System.in);
                System.out.println(targetPath + ":此目录已存在,文件可能会被覆盖!");
//                System.out.println(targetPath + ":此目录已存在,文件可能会被覆盖!按y确定");
//                String next = scanner.next();
//                if (!next.toLowerCase().equals("y")) {
//                    return;
//                }
            } catch (SftpException e) {
                sftp.mkdir(targetPath);
                sftp.cd(targetPath);
            }
            File file = new File(startPath);
            copyFile(sftp, file, sftp.pwd());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.disconnect();
            channel.disconnect();
        }
    }

    public static void copyFile(ChannelSftp sftp, File file, String pwd) {
        if (file.isDirectory()) {
            File[] list = file.listFiles();
            try {
                try {
                    String fileName = file.getName();
                    sftp.cd(pwd);
                    System.out.println("正在创建目录:" + sftp.pwd() + "/" + fileName);
                    sftp.mkdir(fileName);
                    System.out.println("目录创建成功:" + sftp.pwd() + "/" + fileName);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                pwd = pwd + "/" + file.getName();
                try {
                    sftp.cd(file.getName());
                } catch (SftpException e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            for (int i = 0; i < list.length; i++) {
                copyFile(sftp, list[i], pwd);
            }
        } else {
            try {
                sftp.cd(pwd);
            } catch (SftpException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            System.out.println("正在复制文件:" + file.getAbsolutePath());
            InputStream instream = null;
            OutputStream outstream = null;
            try {
                outstream = sftp.put(file.getName());
                instream = new FileInputStream(file);
                byte b[] = new byte[1024];
                int n;
                try {
                    while ((n = instream.read(b)) != -1) {
                        outstream.write(b, 0, n);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (SftpException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    outstream.flush();
                    outstream.close();
                    instream.close();
                } catch (Exception e2) {
                    // TODO: handle exception
                    e2.printStackTrace();
                }
            }
        }
    }

    public static void FTPUpFileByInputStream(Session session, InputStream ips, String filename, String targetPath) {
        Channel channel = null;
        try {
            channel = (Channel) session.openChannel("sftp");
            channel.connect(10000000);
            ChannelSftp sftp = (ChannelSftp) channel;
            try {
                sftp.cd(targetPath);
            } catch (SftpException e) {
                sftp.mkdir(targetPath);
                sftp.cd(targetPath);
            }
            try {
                sftp.cd(sftp.pwd());
            } catch (SftpException e1) {
                e1.printStackTrace();
            }
            OutputStream outstream = null;
            try {
                outstream = sftp.put(filename);
                byte buf[] = new byte[1024];
                int n = 0;
                try {
                    while ((n = ips.read(buf)) != -1) {
                        outstream.write(buf, 0, n);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } finally {
                try {
                    outstream.flush();
                    outstream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.disconnect();
            channel.disconnect();
        }
    }

}

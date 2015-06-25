
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {
    
    public static void main(String args[]){
        /*try {
            URL url = new URL("http://www.journaldugeek.com/wp-content/blogs.dir/1/files/2015/03/minecraft-02-700x393.jpg");
            InputStream is = url.openStream();
            File f = new File(url.getFile());
            System.out.println(f.getName());
            File file = new File("test.jpg");
            file.createNewFile();
            
            DataInputStream dis = new DataInputStream(is);
            FileOutputStream dos = new FileOutputStream(file);
            
            int i = 0;
            
            while((i = dis.read()) != -1)
                dos.write(i);
            
            dos.close();
            dis.close();
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    
}

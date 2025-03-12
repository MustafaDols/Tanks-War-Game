package Game;
import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;
import javax.media.opengl.GLCanvas;
import javax.swing.*;
import java.awt.*;
public class Anim extends JFrame {
    public static void main(String[] args) {
        new Anim();
    }
    GLCanvas glcanvas;
    Tankgame listener;
    Animator animator;
    public Anim() {
        listener = new Tankgame();
        glcanvas = new GLCanvas();
        glcanvas.addGLEventListener(listener);
        glcanvas.addKeyListener(listener);
        glcanvas.addMouseListener(listener);
        glcanvas.addMouseMotionListener(listener);
        getContentPane().add(glcanvas, BorderLayout.CENTER);
        animator = new FPSAnimator(60); 
        animator.add(glcanvas);
        animator.start();
        setTitle("Tank Battle Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        glcanvas.requestFocusInWindow();



}
}

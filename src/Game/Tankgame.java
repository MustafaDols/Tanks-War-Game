package Game;
import Textures.TextureReader;
import com.sun.opengl.util.j2d.TextRenderer;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;
import javafx.embed.swing.JFXPanel;


public class Tankgame extends AnimListener implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {
    String[] textureNames = {
            "enemy up.png", "enemy up.png", "enemy up.png", "enemy up.png",
            "ob (4).png", "ob (4).png", "ob (4).png", "ob (4).png", "ob (4).png", "ob (4).png", "ob (4).png",
            "bullett.png",  "ob (4).png","Bomb.png", "5996.png", "plane1.png","heart.png","HighScore.png","gameover.png","wall.png", "winn.png","ground.jpg","B2.png","halp.jpg"
    };
    TextureReader.Texture[] texture = new TextureReader.Texture[textureNames.length];
    int[] textures = new int[textureNames.length];
    float[] planeX = new float[5];
    float[] planeY = new float[5];
    float[] planeSpeed = new float[5];
    int planeCount = 1;
    float[] parachuteX = new float[5];
    float[] parachuteY = new float[5];
    int parachuteCount = 1;
    int ninjaStarSpeed = 5;
    int animationIndex = 0;
    int maxWidth = 100;
    int maxHeight = 100;
    int x = maxWidth / 2, y = 0;
    int x1 = maxWidth / 3, y1 = maxHeight - 10;
    int highScore=0;
    boolean isStarted = false;
    boolean isPaused = true;
    int health = 7; // عدد القلوب المتبقية
    int x2 = (maxWidth + 30) / 2, y2 = 0; // موقع الدبابة الثانية
    int score = 0;  // متغير لتخزين النتيجة
    int score1 = 0;
    ArrayList<Bullet> bullets = new ArrayList<>();// لتخزين الطلقات
    List<Bullet> missiles = new ArrayList<>(); // قائمة الصواريخ
    int missileTimer = 0;
    ArrayList<Explosion> explosions = new ArrayList<>(); // لتخزين الانفجارات
    private GLU glu;
    private boolean[] selected = new boolean[4]; // 1 Player, 2 Players, Target Practice, Return to Title
    private boolean[] selected2 = new boolean[3];
    private TextRenderer textRenderer;
    private int width = 800;
    private int height = 600;
    private String currentScreen = "menu"; // Track the current screen
    private String currentscreen2 = "game";
    private SoundPlayer soundPlayer;
    String userName = "Guest1"; // تعيين اسم افتراضي إذا لم يتم إدخال اسم
    String userName1 = "Guest2"; // تعيين اسم افتراضي إذا لم يتم إدخال اسم
    float constantSpeed = 3;
    private long startTime = 0;
    private long gameDuration = 60000; // 60 ثانية
    private boolean gameOver = false;
    private float translateX1 = 0.0f; // موضع الصورة الأولى
    private float translateX2 = 1.0f; // موضع الصورة الثانية (تبدأ بجانب الصورة الأولى مباشرة)
    private float translationSpeed = 0.006f; // سرعة الحركة
    private int switchScoreThreshold = 50; // النقاط التي تبدأ عندها الحركة



    public Tankgame() {
        textRenderer = new TextRenderer(new Font("Helvetica", Font.BOLD, 36));
    }


    public BitSet keyBits = new BitSet(256);

    public void keyPressed(final KeyEvent event) {
        int keyCode = event.getKeyCode();
        keyBits.set(keyCode);

        // تحقق من المدخلات بناءً على المفاتيح
        if (currentScreen.equals("menu")) {
            // في الواجهة الرئيسية
            if (keyCode == KeyEvent.VK_1) {
                selectMenuOption(0); // اختر "1 Player"
            } else if (keyCode == KeyEvent.VK_2) {
                selectMenuOption(1); // اختر "2 Players"
            } else if (keyCode == KeyEvent.VK_3) {
                selectMenuOption(2); // اختر "Target Practice"
            } else if (keyCode == KeyEvent.VK_4) {
                selectMenuOption(3); // اختر "Return to Title"
            } else if (keyCode == KeyEvent.VK_ESCAPE) {
                System.exit(0); // الخروج من اللعبة فقط إذا كنت في القائمة الرئيسية
            }
        } else if (currentScreen.equals("game")) {
            // في واجهة الصعوبة
            if (keyCode == KeyEvent.VK_E) {
                selectMenuOption2(0); // اختر "Easy"
            } else if (keyCode == KeyEvent.VK_M) {
                selectMenuOption2(1); // اختر "Medium"
            } else if (keyCode == KeyEvent.VK_H) {
                selectMenuOption2(2); // اختر "Hard"
            } else if (keyCode == KeyEvent.VK_ESCAPE) {

                currentScreen = "game"; // العودة إلى الواجهة الرئيسية فقط إذا كنت في واجهة الصعوبة
            }
        } else if (currentScreen.equals("game2")) {
            if (keyCode == KeyEvent.VK_E) {
                selectMenuOption2(3); // اختر "Easy"
            } else if (keyCode == KeyEvent.VK_A) {
                selectMenuOption2(4); // اختر "Medium"
            } else if (keyCode == KeyEvent.VK_Q) {


                currentScreen = "menu"; // العودة إلى الواجهة الرئيسية فقط إذا كنت في واجهة الصعوبة
            }
        }
    }


    @Override
    public void keyReleased(final KeyEvent event) {
        int keyCode = event.getKeyCode();
        keyBits.clear(keyCode);
    }

    @Override
    public void keyTyped(final KeyEvent event) {
    }

    public boolean isKeyPressed(final int keyCode) {
        return keyBits.get(keyCode);
    }

    @Override
    public void init(GLAutoDrawable gld) {

        GL gl = gld.getGL();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);    //This Will Clear The Background Color To Black

        gl.glEnable(GL.GL_TEXTURE_2D);  // Enable Texture Mapping
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glGenTextures(textureNames.length, textures, 0);

        for(int i = 0; i < textureNames.length; i++){
            try {
                texture[i] = TextureReader.readTexture(assetsFolderName + "//" + textureNames[i] , true);
                gl.glBindTexture(GL.GL_TEXTURE_2D, textures[i]);

//                mipmapsFromPNG(gl, new GLU(), texture[i]);
                new GLU().gluBuild2DMipmaps(
                        GL.GL_TEXTURE_2D,
                        GL.GL_RGBA, // Internal Texel Format,
                        texture[i].getWidth(), texture[i].getHeight(),
                        GL.GL_RGBA, // External format from image,
                        GL.GL_UNSIGNED_BYTE,
                        texture[i].getPixels() // Imagedata
                );
            } catch( IOException e ) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        try {
            File file = new File("highscore.txt");
            if (file.exists()) { // إذا كان الملف موجودًا
                Scanner scanner = new Scanner(file);
                if (scanner.hasNextInt()) { // إذا كان يحتوي على رقم
                    highScore = scanner.nextInt();
                } else {
                    highScore = 0; // إذا كان الملف فارغًا
                }
                scanner.close();
            } else {
                highScore = 0; // إذا لم يكن الملف موجودًا
            }
            System.out.println("High Score loaded: " + highScore);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            FileWriter writer = new FileWriter("highscore.txt");
            writer.write(String.valueOf(highScore));
            writer.close();
            System.out.println("Score written successfully.");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
            e.printStackTrace();
        }


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                FileWriter writer = new FileWriter("highscore.txt");
                writer.write(String.valueOf(highScore));
                writer.close();
                System.out.println("High Score saved: " + highScore);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        // Initialize sound and play menu music
        soundPlayer = new SoundPlayer();
        soundPlayer.playSound("1 - Track 1.mp3"); // Replace with the actual path
    }
    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL gl = glAutoDrawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT); // Clear the screen

        if (currentScreen.equals("menu")) {
            drawBackground(gl, textures.length - 3); // Draw the background for the menu
            drawMenuOptions(gl); // Draw the menu options
        } else if (currentScreen.equals("game")) {
            if (currentscreen2.equals("game")) {
                drawBackground(gl, textures.length - 2);
                drawMenuOptions2(gl);
            } else if (currentscreen2.equals("Easy game") && currentScreen.equals("game")) {
                drawBackground2(gl,textureNames.length-4,textureNames.length-4);
                renderText(userName, -0.8f, 0.8f, Color.WHITE);
                renderText("Score:"+String.valueOf(score), 0.8f, 0.8f, Color.WHITE);

                if (score==200) {// إذا انتهى الوقت
                    gameOver = true; // قم بإنهاء اللعبة
                }
                if (gameOver) {
                    soundPlayer.playSound("1 - Track 1.mp3");
                    DrawSprite2(gl, maxWidth / 2 - 5, maxHeight / 2 - 5, textureNames.length - 4, 10);
                    renderText("Your Score:"+String.valueOf(score), 0.0f, 0.8f, Color.GREEN);
                    return; // إيقاف اللعبة
                }
                if (health <= 0) {
                    DrawSprite2(gl, maxWidth / 2 - 5, maxHeight / 2 - 5, textureNames.length - 6, 10);
                    gl.glPushMatrix();
                    gl.glScaled(3,3,3);
                    renderText("Score:"+String.valueOf(score), 0, 0, Color.WHITE);
                    gl.glPopMatrix();
                    if (score > highScore) {
                        highScore = score;
                        if (score > highScore) {
                            highScore = score; // تحديث الـ High Score
                            try {
                                FileWriter writer = new FileWriter("highscore.txt");
                                writer.write(String.valueOf(highScore)); // الكتابة في الملف
                                writer.close();
                                System.out.println("High Score updated: " + highScore);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    return;
                }

                if (isPaused) {
                    DrawSprite2(gl, maxWidth / 2 - 5, maxHeight / 2 - 5, textureNames.length - 3, 10);
                    return;
                }

                y1--;
                handleKeyPress();

                // رسم القلوب
                for (int i = 0; i < health; i++) { // يتم رسم عدد القلوب بناءً على قيمة الصحة
                    DrawSprite2(gl, 1 + i * 4, maxHeight - 10, 16, 0.4f); // رسم قلب ممتلئ فقط
                }

                // تحريك الطائرات
                for (int i = 0; i < planeCount; i++) {
                    planeX[i] -= (constantSpeed-2.5); // استخدام السرعة الثابتة
                    if (planeX[i] < -50) {
                        planeX[i] = maxWidth;
                        planeY[i] = (int) (Math.random() * (maxHeight) + (maxHeight / 2));
                        if (health <= 0) {
                            return;
                        }
                    }
                    DrawSprite2(gl, (int) planeX[i], (int) planeY[i], textureNames.length - 9, 4);
                }
                // إطلاق الصواريخ من الطائرات
                missileTimer++;
                if (missileTimer >= 100) { // إطلاق صاروخ كل 100 إطار
                    for (int i = 0; i < planeCount; i++) {
                        missiles.add(new Bullet((int) planeX[i], (int) planeY[i])); // إضافة صاروخ جديد
                    }
                    missileTimer = 0; // إعادة تعيين العداد
                }
                // تحريك الصواريخ
                for (int i = 0; i < missiles.size(); i++) {
                    Bullet missile = missiles.get(i);
                    missile.y -= (constantSpeed-1); // الصاروخ يتحرك لأسفل
                    DrawSprite2(gl, missile.x, missile.y, textureNames.length - 11, 0.5f); // رسم الصاروخ
                    // التحقق من التصادم مع اللاعب
                    double dist = sqrdDistance(missile.x, missile.y, x, y);
                    if (dist <= 50) {
                        System.out.println("Hit Player!");
                        soundPlayer.playSoundForDuration("explosion-42132.mp3", 1);
                        missiles.remove(i);
                        health--; // تقليل الصحة عند الإصابة
                        if (health <= 0) {
                            return;
                        }
                        break;
                    }
                    // إزالة الصاروخ إذا خرج من الشاشة
                    if (missile.y < 0) {
                        missiles.remove(i);
                        break;
                    }
                }
                // تحريك البرشوت
                for (int i = 0; i < parachuteCount; i++) {
                    parachuteY[i] -= (constantSpeed - 2.5); // استخدام السرعة الثابتة
                    if (parachuteY[i] <= 0) {
                        parachuteX[i] = (int) (Math.random() * maxWidth);
                        parachuteY[i] = maxHeight;
                        soundPlayer.playSoundForDuration("explosion-42132.mp3", 1);
                        health--; // تقليل الصحة عند وصول البرشوت للأرض
                        if (health <= 0) {
                            return;
                        }
                    }
                    DrawSprite2(gl, (int) parachuteX[i], (int) parachuteY[i], 12, 1);
                }

                // رسم باقي العناصر والطلقات
                for (int i = 0; i < bullets.size(); i++) {
                    Bullet bullet = bullets.get(i);
                    bullet.y += ninjaStarSpeed;

                    DrawSprite2(gl, bullet.x, bullet.y, 11, 0.5f);

                    // التحقق من الاصطدام بالجنود
                    for (int j = 0; j < parachuteCount; j++) {
                        double dist = sqrdDistance(bullet.x, bullet.y, (int) parachuteX[j], (int) parachuteY[j]);
                        if (dist <= 50) {
                            System.out.println("Hit Parachute!");
                            soundPlayer.playSoundForDuration("explosion-42132.mp3", 1);
                            bullets.remove(i);
                            explosions.add(new Explosion((int) parachuteX[j], (int) parachuteY[j]));
                            parachuteX[j] = (int) (Math.random() * maxWidth);
                            parachuteY[j] = maxHeight;
                            score += 10;
                            break;
                        }
                    }

                    // التحقق من الاصطدام بالطائرات
                    for (int j = 0; j < planeCount; j++) {
                        double dist = sqrdDistance(bullet.x, bullet.y, (int) planeX[j], (int) planeY[j]);
                        if (dist <= 50) {
                            System.out.println("Hit Plane!");
                            soundPlayer.playSoundForDuration("explosion-42132.mp3", 1);
                            bullets.remove(i);
                            explosions.add(new Explosion((int) planeX[j], (int) planeY[j]));
                            planeX[j] = maxWidth;
                            planeY[j] = (int) (Math.random() * (maxHeight) + (maxHeight / 2));
                            score += 20;

                            break;
                        }
                    }
                    if (bullet.y > maxHeight) {
                        bullets.remove(i);
                        break;
                    }
                }
                animationIndex = animationIndex % 4;
                DrawSprite2(gl, x, y, animationIndex, 1);

                // رسم الانفجارات
                for (int i = 0; i < explosions.size(); i++) {
                    Explosion explosion = explosions.get(i);
                    explosion.timeLeft--;
                    DrawSprite2(gl, explosion.x, explosion.y, 14, 1);

                    if (explosion.timeLeft <= 0) {
                        explosions.remove(i);
                    }
                }
            }else if (currentscreen2.equals("medium game")){
                drawBackground2(gl,textureNames.length-4,textureNames.length-4);
                renderText(userName, -0.8f, 0.8f, Color.WHITE);
                renderText("Score:"+String.valueOf(score), 0.8f, 0.8f, Color.WHITE);
                if (score==400) {// إذا انتهى الوقت
                    gameOver = true; // قم بإنهاء اللعبة
                }
                if (gameOver) {
                    soundPlayer.playSound("1 - Track 1.mp3");
                    soundPlayer.playSoundForDuration("explosion-42132.mp3", 1);
                    DrawSprite2(gl, maxWidth / 2 - 5, maxHeight / 2 - 5, textureNames.length - 4, 10);
                    renderText("Your Score:"+String.valueOf(score), 0.0f, 0.8f, Color.GREEN);
                    return; // إيقاف اللعبة
                }
                if (health <= 0) {
                    DrawSprite2(gl, maxWidth / 2 - 5, maxHeight / 2 - 5, textureNames.length - 6, 10);
                    gl.glPushMatrix();
                    gl.glScaled(3,3,3);
                    renderText("Score:"+String.valueOf(score), 0, 0, Color.WHITE);
                    gl.glPopMatrix();
                    if (score > highScore) {
                        highScore = score;
                        if (score > highScore) {
                            highScore = score; // تحديث الـ High Score
                            try {
                                FileWriter writer = new FileWriter("highscore.txt");
                                writer.write(String.valueOf(highScore)); // الكتابة في الملف
                                writer.close();
                                System.out.println("High Score updated: " + highScore);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    return;
                }
                if (isPaused) {
                    DrawSprite2(gl, maxWidth / 2 - 5, maxHeight / 2 - 5, textureNames.length - 3, 10);
                    return;
                }
                y1--;
                handleKeyPress();

                // رسم القلوب
                for (int i = 0; i < health; i++) { // يتم رسم عدد القلوب بناءً على قيمة الصحة
                    DrawSprite2(gl, 1 + i * 4, maxHeight - 10, 16, 0.4f); // رسم قلب ممتلئ فقط
                }
                // تحريك الطائرات
                for (int i = 0; i < planeCount; i++) {
                    planeX[i] -= (constantSpeed-2); // استخدام السرعة الثابتة
                    if (planeX[i] < -50) {
                        planeX[i] = maxWidth;
                        planeY[i] = (int) (Math.random() * (maxHeight) + (maxHeight / 2));
                        if (health <= 0) {
                            return;
                        }
                    }
                    DrawSprite2(gl, (int) planeX[i], (int) planeY[i], textureNames.length - 9, 4);
                }
                // إطلاق الصواريخ من الطائرات
                missileTimer++;
                if (missileTimer >= 100) { // إطلاق صاروخ كل 100 إطار
                    for (int i = 0; i < planeCount; i++) {
                        missiles.add(new Bullet((int) planeX[i], (int) planeY[i])); // إضافة صاروخ جديد
                    }
                    missileTimer = 0; // إعادة تعيين العداد
                }
                // تحريك الصواريخ
                for (int i = 0; i < missiles.size(); i++) {
                    Bullet missile = missiles.get(i);
                    missile.y -= (constantSpeed-1); // الصاروخ يتحرك لأسفل
                    DrawSprite2(gl, missile.x, missile.y, textureNames.length - 11, 0.5f); // رسم الصاروخ
                    // التحقق من التصادم مع اللاعب
                    double dist = sqrdDistance(missile.x, missile.y, x, y);
                    if (dist <= 50) {
                        System.out.println("Hit Player!");
                        soundPlayer.playSoundForDuration("explosion-42132.mp3", 1);
                        missiles.remove(i);
                        health--; // تقليل الصحة عند الإصابة
                        if (health <= 0) {
                            return;
                        }
                        break;
                    }
                    // إزالة الصاروخ إذا خرج من الشاشة
                    if (missile.y < 0) {
                        missiles.remove(i);
                        break;
                    }
                }
                // تحريك البرميل
                for (int i = 0; i < parachuteCount; i++) {
                    parachuteY[i] -= (constantSpeed - 2); // استخدام السرعة الثابتة
                    if (parachuteY[i] <= 0) {
                        parachuteX[i] = (int) (Math.random() * maxWidth);
                        parachuteY[i] = maxHeight;
                        soundPlayer.playSoundForDuration("explosion-42132.mp3", 1);
                        health--; // تقليل الصحة عند وصول البرشوت للأرض
                        if (health <= 0) {
                            return;
                        }
                    }
                    DrawSprite2(gl, (int) parachuteX[i], (int) parachuteY[i], 12, 1);
                }
                // رسم باقي العناصر والطلقات
                for (int i = 0; i < bullets.size(); i++) {
                    Bullet bullet = bullets.get(i);
                    bullet.y += ninjaStarSpeed;
                    DrawSprite2(gl, bullet.x, bullet.y, 11, 0.5f);
                    // التحقق من الاصطدام بالجنود
                    for (int j = 0; j < parachuteCount; j++) {
                        double dist = sqrdDistance(bullet.x, bullet.y, (int) parachuteX[j], (int) parachuteY[j]);
                        if (dist <= 50) {
                            System.out.println("Hit Parachute!");
                            soundPlayer.playSoundForDuration("explosion-42132.mp3", 1);
                            bullets.remove(i);
                            explosions.add(new Explosion((int) parachuteX[j], (int) parachuteY[j]));
                            parachuteX[j] = (int) (Math.random() * maxWidth);
                            parachuteY[j] = maxHeight;
                            score += 10;
                            break;
                        }
                    }
                    // التحقق من الاصطدام بالطائرات
                    for (int j = 0; j < planeCount; j++) {
                        double dist = sqrdDistance(bullet.x, bullet.y, (int) planeX[j], (int) planeY[j]);
                        if (dist <= 50) {
                            System.out.println("Hit Plane!");
                            soundPlayer.playSoundForDuration("explosion-42132.mp3", 1);

                            bullets.remove(i);
                            explosions.add(new Explosion((int) planeX[j], (int) planeY[j]));
                            planeX[j] = maxWidth;
                            planeY[j] = (int) (Math.random() * (maxHeight) + (maxHeight / 2));
                            score += 20;
                            break;
                        }
                    }
                    if (bullet.y > maxHeight) {
                        bullets.remove(i);
                        break;
                    }
                }
                animationIndex = animationIndex % 4;
                DrawSprite2(gl, x, y, animationIndex, 1);
                // رسم الانفجارات
                for (int i = 0; i < explosions.size(); i++) {
                    Explosion explosion = explosions.get(i);
                    explosion.timeLeft--;
                    DrawSprite2(gl, explosion.x, explosion.y, 14, 1);

                    if (explosion.timeLeft <= 0) {
                        explosions.remove(i);
                    }
                }
            }
            else if (currentscreen2.equals("Hard game")) {
                drawBackground2(gl,textureNames.length-4,textureNames.length-4);
                renderText(userName, -0.8f, 0.8f, Color.WHITE);
                renderText("Score:"+String.valueOf(score), 0.8f, 0.8f, Color.WHITE);
                long currentTime = System.currentTimeMillis();
                if (startTime == 0) {
                    startTime = currentTime; // بداية اللعبة
                }
                long elapsedTime = currentTime - startTime; // الوقت المنقضي
                long remainingTime = gameDuration - elapsedTime; // الوقت المتبقي
                if (remainingTime <= 0) {// إذا انتهى الوقت
                    gameOver = true; // قم بإنهاء اللعبة
                }
                if (gameOver) {
                    soundPlayer.playSound("1 - Track 1.mp3");
                    DrawSprite2(gl, maxWidth / 2 - 5, maxHeight / 2 - 5, textureNames.length - 4, 10);
                    renderText("Your Score:"+String.valueOf(score), 0.0f, 0.8f, Color.GREEN);
                    return; // إيقاف اللعبة
                }
                renderText("Time Left: " + remainingTime / 1000 + "s", 0.0f, 0.8f, Color.WHITE); // عرض الوقت المتبقي
                if (health <= 0) {
                    DrawSprite2(gl, maxWidth / 2 - 5, maxHeight / 2 - 5, textureNames.length - 6, 10);
                    gl.glPushMatrix();
                    gl.glScaled(3,3,3);
                    renderText("Score:"+String.valueOf(score), 0, 0, Color.WHITE);
                    gl.glPopMatrix();
                    if (score > highScore) {
                        highScore = score;
                        if (score > highScore) {
                            highScore = score; // تحديث الـ High Score
                            try {
                                FileWriter writer = new FileWriter("highscore.txt");
                                writer.write(String.valueOf(highScore)); // الكتابة في الملف
                                writer.close();
                                System.out.println("High Score updated: " + highScore);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    return;
                }
                if (isPaused) {
                    DrawSprite2(gl, maxWidth / 2 - 5, maxHeight / 2 - 5, textureNames.length - 3, 10);
                    return;
                }
                y1--;
                handleKeyPress();
                // رسم القلوب
                for (int i = 0; i < health; i++) { // يتم رسم عدد القلوب بناءً على قيمة الصحة
                    DrawSprite2(gl, 1 + i * 4, maxHeight - 10, 16, 0.4f); // رسم قلب ممتلئ فقط
                }
                // تحريك الطائرات
                for (int i = 0; i < planeCount; i++) {
                    planeX[i] -= (constantSpeed-2.5); // استخدام السرعة الثابتة
                    if (planeX[i] < -50) {
                        planeX[i] = maxWidth;
                        planeY[i] = (int) (Math.random() * (maxHeight) + (maxHeight / 2));
                        if (health <= 0) {
                            return;
                        }
                    }
                    DrawSprite2(gl, (int) planeX[i], (int) planeY[i], textureNames.length - 9, 4);
                }
                // إطلاق الصواريخ من الطائرات
                missileTimer++;
                if (missileTimer >= 100) { // إطلاق صاروخ كل 100 إطار
                    for (int i = 0; i < planeCount; i++) {
                        missiles.add(new Bullet((int) planeX[i], (int) planeY[i])); // إضافة صاروخ جديد
                    }
                    missileTimer = 0; // إعادة تعيين العداد
                }
                // تحريك الصواريخ
                for (int i = 0; i < missiles.size(); i++) {
                    Bullet missile = missiles.get(i);
                    missile.y -= (constantSpeed-1.5); // الصاروخ يتحرك لأسفل

                    DrawSprite2(gl, missile.x, missile.y, textureNames.length - 11, 0.5f); // رسم الصاروخ

                    // التحقق من التصادم مع اللاعب
                    double dist = sqrdDistance(missile.x, missile.y, x, y);
                    if (dist <= 50) {
                        System.out.println("Hit Player!");
                        soundPlayer.playSoundForDuration("explosion-42132.mp3", 1);
                        missiles.remove(i);
                        health--; // تقليل الصحة عند الإصابة
                        if (health <= 0) {
                            return;
                        }
                        break;
                    }
                    // إزالة الصاروخ إذا خرج من الشاشة
                    if (missile.y < 0) {
                        missiles.remove(i);
                        break;
                    }
                }
                // تحريك البرميل
                for (int i = 0; i < parachuteCount; i++) {
                    parachuteY[i] -= (constantSpeed - 1.5); // استخدام السرعة الثابتة
                    if (parachuteY[i] <= 0) {
                        parachuteX[i] = (int) (Math.random() * maxWidth);
                        parachuteY[i] = maxHeight;
                        soundPlayer.playSoundForDuration("explosion-42132.mp3", 1);
                        health--; // تقليل الصحة عند وصول البرشوت للأرض
                        if (health <= 0) {
                            return;
                        }
                    }
                    DrawSprite2(gl, (int) parachuteX[i], (int) parachuteY[i], 12, 1);
                }
                // رسم باقي العناصر والطلقات
                for (int i = 0; i < bullets.size(); i++) {
                    Bullet bullet = bullets.get(i);
                    bullet.y += ninjaStarSpeed;
                    DrawSprite2(gl, bullet.x, bullet.y, 11, 0.5f);
                    // التحقق من الاصطدام بالجنود
                    for (int j = 0; j < parachuteCount; j++) {
                        double dist = sqrdDistance(bullet.x, bullet.y, (int) parachuteX[j], (int) parachuteY[j]);
                        if (dist <= 50) {
                            System.out.println("Hit Parachute!");
                            soundPlayer.playSoundForDuration("explosion-42132.mp3", 1);
                            bullets.remove(i);
                            explosions.add(new Explosion((int) parachuteX[j], (int) parachuteY[j]));
                            parachuteX[j] = (int) (Math.random() * maxWidth);
                            parachuteY[j] = maxHeight;
                            score += 10;
                            break;
                        }
                    }
                    // التحقق من الاصطدام بالطائرات
                    for (int j = 0; j < planeCount; j++) {
                        double dist = sqrdDistance(bullet.x, bullet.y, (int) planeX[j], (int) planeY[j]);
                        if (dist <= 50) {
                            System.out.println("Hit Plane!");
                            soundPlayer.playSoundForDuration("explosion-42132.mp3", 1);
                            bullets.remove(i);
                            explosions.add(new Explosion((int) planeX[j], (int) planeY[j]));
                            planeX[j] = maxWidth;
                            planeY[j] = (int) (Math.random() * (maxHeight) + (maxHeight / 2));
                            score += 20;
                            break;
                        }
                    }
                    if (bullet.y > maxHeight) {
                        bullets.remove(i);
                        break;
                    }
                }
                animationIndex = animationIndex % 4;
                DrawSprite2(gl, x, y, animationIndex, 1);
                // رسم الانفجارات
                for (int i = 0; i < explosions.size(); i++) {
                    Explosion explosion = explosions.get(i);
                    explosion.timeLeft--;
                    DrawSprite2(gl, explosion.x, explosion.y, 14, 1);

                    if (explosion.timeLeft <= 0) {
                        explosions.remove(i);
                    }
                }
            }
           // soundPlayer.playSound("2 - Track 2.mp3");
        }
        else if (currentScreen.equals("help")) {
           drawBackground(gl,textures.length-1);
            soundPlayer.playSound("2 - Track 2.mp3");
            handleKeyPress3();
        }
        else if (currentScreen.equals("selected")) {
            soundPlayer.playSound("2 - Track 2.mp3");
            drawBackground(gl,textures.length-7);
            renderText("HighScore : " + highScore,0.0f, -0.4f, Color.WHITE);
            handleKeyPress3();
        }
        else if (currentScreen.equals("game2")) {
            if (currentscreen2.equals("game")) {
                soundPlayer.playSound("2 - Track 2.mp3");
                renderText(userName, -0.8f, 0.9f, Color.WHITE);
                renderText(userName1, 0.8f, 0.9f, Color.WHITE);

                drawBackground(gl, textures.length - 2);
                drawMenuOptions3(gl);
                System.out.println("CurrentScreen: " + currentScreen);
                System.out.println("CurrentScreen2: " + currentscreen2);

            }

        else if  (currentscreen2.equals("1 V 1game2")&&currentScreen.equals("game2")) {

                System.out.println("play");
                drawBackground2(gl, textureNames.length - 4, textureNames.length - 4);
                renderText(userName, -0.8f, 0.8f, Color.WHITE);
                renderText("Score:"+String.valueOf(score), -0.8f, 0.6f, Color.WHITE);
                renderText(userName1, 0.8f, 0.8f, Color.WHITE);
                renderText("Score:" + String.valueOf(score1), 0.8f, 0.6f, Color.WHITE);
                long currentTime = System.currentTimeMillis();
                if (startTime == 0) {
                    startTime = currentTime; // بداية اللعبة
                }
                long elapsedTime = currentTime - startTime; // الوقت المنقضي
                long remainingTime = gameDuration - elapsedTime; // الوقت المتبقي
                if (remainingTime <= 0) {// إذا انتهى الوقت
                    gameOver = true;// قم بإنهاء اللعبة
                    handleKeyPress2();
                }
                if (gameOver) {// إيقاف اللعبة
                    renderText("Game Over!", 0.0f, 0.0f, Color.RED);
                    soundPlayer.playSound("2 - Track 2.mp3");

                    if (score > score1) {
                        soundPlayer.playSound("2 - Track 2.mp3");
                        renderText(userName + " Wins!", 0.0f, 0.5f, Color.GREEN);
                    } else if (score1 > score) {
                        soundPlayer.playSound("2 - Track 2.mp3");
                        renderText(userName1 + " Wins!", 0.0f, 0.5f, Color.GREEN);
                    } else {
                        renderText("It's a Draw!", 0.0f, 0.5f, Color.YELLOW);
                    }
                    renderText("Player 1 Score: " + score, -0.4f, 0.4f, Color.WHITE);
                    renderText("Player 2 Score: " + score1, 0.4f, 0.4f, Color.WHITE);
                    return; // إيقاف اللعبة
                }
                renderText("Time Left: " + remainingTime / 1000 + "s", 0.0f, 0.8f, Color.WHITE);// عرض الوقت المتبقي
               if (health <= 0) {
                   DrawSprite2(gl, maxWidth / 2 - 5, maxHeight / 2 - 5, textureNames.length - 6, 10);
                    return;
                }

                if (isPaused) {
                    DrawSprite2(gl, maxWidth / 2 - 5, maxHeight / 2 - 5, textureNames.length - 3, 10);
                    return;
                }
                y1--;
                handleKeyPress();
                // تحريك الطائرات
                // تحريك الطائرات
                for (int i = 0; i < planeCount; i++) {
                    planeX[i] -= (constantSpeed-2); // استخدام السرعة الثابتة
                    if (planeX[i] < -50) {
                        planeX[i] = maxWidth;
                        planeY[i] = (int) (Math.random() * (maxHeight) + (maxHeight / 2));
                        if (health <= 0) {
                            return;
                        }
                    }
                    DrawSprite2(gl, (int) planeX[i], (int) planeY[i], textureNames.length - 9, 4);
                }
                // إطلاق الصواريخ من الطائرات
                missileTimer++;
                if (missileTimer >= 100) { // إطلاق صاروخ كل 100 إطار
                    for (int i = 0; i < planeCount; i++) {
                        missiles.add(new Bullet((int) planeX[i], (int) planeY[i])); // إضافة صاروخ جديد
                    }
                    missileTimer = 0; // إعادة تعيين العداد
                }
                // تحريك الصواريخ
                for (int i = 0; i < missiles.size(); i++) {
                    Bullet missile = missiles.get(i);
                    missile.y -= (constantSpeed-1); // الصاروخ يتحرك لأسفل
                    DrawSprite2(gl, missile.x, missile.y, textureNames.length - 11, 0.5f); // رسم الصاروخ
                    // التحقق من التصادم مع اللاعب
                    double dist = sqrdDistance(missile.x, missile.y, x, y);
                    if (dist <= 50) {
                        System.out.println("Hit Player!");
                        soundPlayer.playSoundForDuration("explosion-42132.mp3", 1);
                        missiles.remove(i);
                      //  health--; // تقليل الصحة عند الإصابة
                        if (health <= 0) {
                            return;
                        }
                        break;
                    }
                    // إزالة الصاروخ إذا خرج من الشاشة
                    if (missile.y < 0) {
                        missiles.remove(i);
                        break;
                    }
                }
                // تحريك البرميل
                for (int i = 0; i < parachuteCount; i++) {
                    parachuteY[i] -= (constantSpeed - 2); // استخدام السرعة الثابتة
                    if (parachuteY[i] <= 0) {
                        parachuteX[i] = (int) (Math.random() * maxWidth);
                        parachuteY[i] = maxHeight;
                        soundPlayer.playSoundForDuration("explosion-42132.mp3", 1);
                       // health--; // تقليل الصحة عند وصول البرشوت للأرض
                        if (health <= 0) {
                            return;
                        }
                    }
                    DrawSprite2(gl, (int) parachuteX[i], (int) parachuteY[i], 12, 1);
                }
                // رسم باقي العناصر والطلقات
                for (int i = 0; i < bullets.size(); i++) {
                    Bullet bullet = bullets.get(i);
                    bullet.y += ninjaStarSpeed;
                    DrawSprite2(gl, bullet.x, bullet.y, 11, 0.5f);
                    // التحقق من الاصطدام بالجنود
                    for (int j = 0; j < parachuteCount; j++) {
                        double dist = sqrdDistance(bullet.x, bullet.y, (int) parachuteX[j], (int) parachuteY[j]);
                        if (dist <= 50) {
                            System.out.println("Hit Parachute!");
                            soundPlayer.playSoundForDuration("explosion-42132.mp3", 1);
                            bullets.remove(i);
                            explosions.add(new Explosion((int) parachuteX[j], (int) parachuteY[j]));
                            parachuteX[j] = (int) (Math.random() * maxWidth);
                            parachuteY[j] = maxHeight;
                            if (bullet.tankId==1){
                                score+=10;
                            }
                            else if (bullet.tankId==2){
                                score1+=10;

                            }
                            break;
                        }
                    }
                    // التحقق من الاصطدام بالطائرات
                    for (int j = 0; j < planeCount; j++) {
                        double dist = sqrdDistance(bullet.x, bullet.y, (int) planeX[j], (int) planeY[j]);
                        if (dist <= 50) {
                            System.out.println("Hit Plane!");
                            soundPlayer.playSoundForDuration("explosion-42132.mp3", 1);
                            bullets.remove(i);
                            explosions.add(new Explosion((int) planeX[j], (int) planeY[j]));
                            planeX[j] = maxWidth;
                            planeY[j] = (int) (Math.random() * (maxHeight) + (maxHeight / 2));

                            if (bullet.tankId==1){
                                score+=20;
                            }
                            else if (bullet.tankId==2){
                                score1+=20;

                            }
                            break;
                        }
                    }
                    if (bullet.y > maxHeight) {
                        bullets.remove(i);
                        break;
                    }
                }
                animationIndex = animationIndex % 4;
                DrawSprite2(gl, x, y, animationIndex, 1);
                DrawSprite2(gl, x2, y2, animationIndex, 1);
                for (int i = 0; i < explosions.size(); i++) {
                    Explosion explosion = explosions.get(i);
                    explosion.timeLeft--;
                    DrawSprite2(gl, explosion.x, explosion.y, 14, 1);
                    soundPlayer.playSoundForDuration("explosion-42132.mp3", 1);
                    if (explosion.timeLeft <= 0) {
                        explosions.remove(i);
                    }
                }
            }

            else if  (currentscreen2.equals("1 V Computergame2")&&currentScreen.equals("game2")) {

                System.out.println("play");
                drawBackground2(gl, textureNames.length - 4, textureNames.length - 4);
                renderText(userName, -0.8f, 0.8f, Color.WHITE);
                renderText("Score:"+String.valueOf(score), -0.8f, 0.6f, Color.WHITE);
                renderText(userName1, 0.8f, 0.8f, Color.WHITE);
                renderText("Score:" + String.valueOf(score1), 0.8f, 0.6f, Color.WHITE);
                long currentTime = System.currentTimeMillis();
                if (startTime == 0) {
                    startTime = currentTime; // بداية اللعبة
                }
                long elapsedTime = currentTime - startTime; // الوقت المنقضي
                long remainingTime = gameDuration - elapsedTime; // الوقت المتبقي
                if (remainingTime <= 0) {// إذا انتهى الوقت
                    gameOver = true;// قم بإنهاء اللعبة
                    handleKeyPress2();
                }
                if (gameOver) {// إيقاف اللعبة
                    renderText("Game Over!", 0.0f, 0.0f, Color.RED);

                    if (score > score1) {
                        renderText(userName + " Wins!", 0.0f, 0.5f, Color.GREEN);
                    } else if (score1 > score) {
                        renderText(userName1 + " Wins!", 0.0f, 0.5f, Color.GREEN);
                    } else {
                        renderText("It's a Draw!", 0.0f, 0.5f, Color.YELLOW);
                    }
                    renderText("Player 1 Score: " + score, -0.4f, 0.4f, Color.WHITE);
                    renderText("Player 2 Score: " + score1, 0.4f, 0.4f, Color.WHITE);
                    return; // إيقاف اللعبة
                }
                renderText("Time Left: " + remainingTime / 1000 + "s", 0.0f, 0.8f, Color.WHITE);// عرض الوقت المتبقي
                if (health <= 0) {
                    DrawSprite2(gl, maxWidth / 2 - 5, maxHeight / 2 - 5, textureNames.length - 6, 10);
                    return;
                }

                if (isPaused) {
                    DrawSprite2(gl, maxWidth / 2 - 5, maxHeight / 2 - 5, textureNames.length - 3, 10);
                    return;
                }
                y1--;
                handleKeyPress();
                // تحريك الطائرات
                // تحريك الصواريخ
                for (int i = 0; i < planeCount; i++) {
                    planeX[i] -= (constantSpeed-2); // استخدام السرعة الثابتة
                    if (planeX[i] < -50) {
                        planeX[i] = maxWidth;
                        planeY[i] = (int) (Math.random() * (maxHeight) + (maxHeight / 2));
                        if (health <= 0) {
                            return;
                        }
                    }
                    DrawSprite2(gl, (int) planeX[i], (int) planeY[i], textureNames.length - 9, 4);
                }
                // إطلاق الصواريخ من الطائرات
                missileTimer++;
                if (missileTimer >= 100) { // إطلاق صاروخ كل 100 إطار
                    for (int i = 0; i < planeCount; i++) {
                        missiles.add(new Bullet((int) planeX[i], (int) planeY[i])); // إضافة صاروخ جديد
                    }
                    missileTimer = 0; // إعادة تعيين العداد
                }
                for (int i = 0; i < missiles.size(); i++) {
                    Bullet missile = missiles.get(i);
                    missile.y -= (constantSpeed-1); // الصاروخ يتحرك لأسفل
                    DrawSprite2(gl, missile.x, missile.y, textureNames.length - 11, 0.5f); // رسم الصاروخ
                    // التحقق من التصادم مع اللاعب
                    double dist = sqrdDistance(missile.x, missile.y, x, y);
                    if (dist <= 50) {
                        System.out.println("Hit Player!");
                        missiles.remove(i);
                        //  health--; // تقليل الصحة عند الإصابة
                        if (health <= 0) {
                            return;
                        }
                        break;
                    }
                    // إزالة الصاروخ إذا خرج من الشاشة
                    if (missile.y < 0) {
                        missiles.remove(i);
                        break;
                    }
                }
                // تحريك البرميل
                for (int i = 0; i < parachuteCount; i++) {
                    parachuteY[i] -= (constantSpeed - 2); // استخدام السرعة الثابتة
                    if (parachuteY[i] <= 0) {
                        parachuteX[i] = (int) (Math.random() * maxWidth);
                        parachuteY[i] = maxHeight;
                        // health--; // تقليل الصحة عند وصول البرشوت للأرض
                        if (health <= 0) {
                            return;
                        }
                    }
                    DrawSprite2(gl, (int) parachuteX[i], (int) parachuteY[i], 12, 1);
                }
                // رسم باقي العناصر والطلقات
                for (int i = 0; i < bullets.size(); i++) {
                    Bullet bullet = bullets.get(i);
                    bullet.y += ninjaStarSpeed;
                    DrawSprite2(gl, bullet.x, bullet.y, 11, 0.5f);
                    // التحقق من الاصطدام بالجنود
                    for (int j = 0; j < parachuteCount; j++) {
                        double dist = sqrdDistance(bullet.x, bullet.y, (int) parachuteX[j], (int) parachuteY[j]);
                        if (dist <= 50) {
                            System.out.println("Hit Parachute!");
                            bullets.remove(i);
                            explosions.add(new Explosion((int) parachuteX[j], (int) parachuteY[j]));
                            parachuteX[j] = (int) (Math.random() * maxWidth);
                            parachuteY[j] = maxHeight;
                            if (bullet.tankId==1){
                                score+=10;
                            }
                            else if (bullet.tankId==2){
                                score1+=10;

                            }
                            break;
                        }
                    }
                    // التحقق من الاصطدام بالطائرات
                    for (int j = 0; j < planeCount; j++) {
                        double dist = sqrdDistance(bullet.x, bullet.y, (int) planeX[j], (int) planeY[j]);
                        if (dist <= 50) {
                            System.out.println("Hit Plane!");
                            bullets.remove(i);
                            explosions.add(new Explosion((int) planeX[j], (int) planeY[j]));
                            planeX[j] = maxWidth;
                            planeY[j] = (int) (Math.random() * (maxHeight) + (maxHeight / 2));

                            if (bullet.tankId==1){
                                score+=20;
                            }
                            else if (bullet.tankId==2){
                                score1+=20;

                            }
                            break;
                        }
                    }
                    if (bullet.y > maxHeight) {
                        bullets.remove(i);
                        break;
                    }
                }
                animationIndex = animationIndex % 4;
                DrawSprite2(gl, x, y, animationIndex, 1);
                DrawSprite2(gl, x2, y2, animationIndex, 1);
                for (int i = 0; i < explosions.size(); i++) {
                    Explosion explosion = explosions.get(i);
                    explosion.timeLeft--;
                    DrawSprite2(gl, explosion.x, explosion.y, 14, 1);
                    soundPlayer.playSoundForDuration("explosion-42132.mp3", 1);
                    if (explosion.timeLeft <= 0) {
                        explosions.remove(i);
                    }
                }
            }
        }
    }
    public double sqrdDistance(int x, int y, int x1, int y1) {
        return Math.pow(x - x1, 2) + Math.pow(y - y1, 2);
    }
    private void drawBackground(GL gl,int index) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[index]); // Use the first texture for the background
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex2f(-1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex2f(1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex2f(1.0f, 1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex2f(-1.0f, 1.0f);
        gl.glEnd();
        gl.glDisable(GL.GL_BLEND);
    }
    public void DrawSprite2(GL gl, int x, int y, int index, float scale) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[index]);
        gl.glPushMatrix();
        gl.glTranslated(x / (maxWidth / 2.0) - 0.9, y / (maxHeight / 2.0) - 0.9, 0);
        gl.glScaled(0.1 * scale, 0.1 * scale, 1);
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();
        gl.glDisable(GL.GL_BLEND);
    }
    private void drawMenuOptions(GL gl) {
        // إعداد المتغيرات العامة لرسم الأزرار
        float buttonStartY = 0.4f;
        float buttonSpacing = 0.2f;
        float buttonWidth = 0.8f;
        float buttonHeight = 0.12f;
        // رسم الأزرار
        drawSolidButton(gl, -buttonWidth / 2, buttonStartY, buttonWidth, buttonHeight, "1 Player", selected[0]);
        drawSolidButton(gl, -buttonWidth / 2, buttonStartY - buttonSpacing, buttonWidth, buttonHeight, "2 Players", selected[1]);
        drawSolidButton(gl, -buttonWidth / 2, buttonStartY - 2 * buttonSpacing, buttonWidth, buttonHeight, "Help", selected[2]);
        drawSolidButton(gl, -buttonWidth / 2, buttonStartY - 3 * buttonSpacing, buttonWidth, buttonHeight, "High Score", selected[3]);
        renderText("ESC to Exit", -0.7f, -0.9f, Color.WHITE);
    }
    private void drawMenuOptions2(GL gl) {
        // إعداد المتغيرات العامة لرسم الأزرار
        float buttonStartY = 0.4f;
        float buttonSpacing = 0.2f;
        float buttonWidth = 0.8f;
        float buttonHeight = 0.12f;
        // رسم الأزرار
        drawSolidButton(gl, -buttonWidth / 2, buttonStartY, buttonWidth, buttonHeight, "Easy", selected2[0]);
        drawSolidButton(gl, -buttonWidth / 2, buttonStartY - buttonSpacing, buttonWidth, buttonHeight, "Medium", selected2[1]);
        drawSolidButton(gl, -buttonWidth / 2, buttonStartY - 2 * buttonSpacing, buttonWidth, buttonHeight, "Hard", selected2[2]);
        renderText("ESC to Exit", -0.7f, -0.9f, Color.WHITE);
    }
    private void drawMenuOptions3(GL gl) {
        // إعداد المتغيرات العامة لرسم الأزرار
        float buttonStartY = 0.4f;
        float buttonSpacing = 0.2f;
        float buttonWidth = 0.8f;
        float buttonHeight = 0.12f;
        // رسم الأزرار
        drawSolidButton(gl, -buttonWidth / 2, buttonStartY, buttonWidth, buttonHeight, "1 V 1", selected2[0]);
        drawSolidButton(gl, -buttonWidth / 2, buttonStartY - buttonSpacing, buttonWidth, buttonHeight, "1 V Computer", selected2[1]);

        renderText("ESC to Exit", -0.7f, -0.9f, Color.WHITE);
    }
    private void drawSolidButton(GL gl, float x, float y, float width, float height, String text, boolean isSelected) {
        // رسم الظل (خلف الزر)
        gl.glColor4f(0.2f, 0.2f, 0.2f, 0.8f); // لون داكن للظل
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(x - 0.01f, y + 0.01f);
        gl.glVertex2f(x + width + 0.01f, y + 0.01f);
        gl.glVertex2f(x + width + 0.01f, y - height - 0.01f);
        gl.glVertex2f(x - 0.01f, y - height - 0.01f);
        gl.glEnd();
        if (isSelected) {
            gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f); // زر محدد (أحمر بالكامل)
        } else {
            gl.glColor4f(1.0f, 0.0f, 0.0f, 0.7f); // زر غير محدد (شفافية أقل)
        }
        // رسم الزر الأساسي
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + width, y);
        gl.glVertex2f(x + width, y - height);
        gl.glVertex2f(x, y - height);
        gl.glEnd();
        // رسم الإطار البارز حول الزر
        gl.glColor4f(1.0f, 0.5f, 0.5f, 1.0f); // لون أفتح للإطار
        gl.glLineWidth(2.0f); // سماكة الإطار
        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + width, y);
        gl.glVertex2f(x + width, y - height);
        gl.glVertex2f(x, y - height);
        gl.glEnd();
        renderText(text, x + width / 2 - 0.1f, y - height / 2 + 0.03f, Color.WHITE);
    }
    public class SoundPlayer {
        private MediaPlayer mediaPlayer;
        private boolean isSoundPlaying = false;
        public void playSound(String resourcePath) {
            try {
                new JFXPanel();
                // تحميل ملف الصوت
                URL soundURL = getClass().getResource(resourcePath);
                if (soundURL == null) {
                    System.err.println("Sound file not found: " + resourcePath);
                    return;
                }
                // تشغيل الصوت إذا لم يكن قيد التشغيل
                if (mediaPlayer == null || !isSoundPlaying) {
                    Media soundTrack = new Media(soundURL.toURI().toString());
                    mediaPlayer = new MediaPlayer(soundTrack);
                    mediaPlayer.play();
                    isSoundPlaying = true;

                    // إعادة تعيين الحالة عند انتهاء الصوت
                    mediaPlayer.setOnEndOfMedia(() -> {
                        isSoundPlaying = false;
                        System.out.println("Playback finished.");
                    });

                    System.out.println("Playing sound: " + resourcePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error playing sound: " + resourcePath);
            }
        }
        public void stopSound() {
            if (mediaPlayer != null && isSoundPlaying) {
                mediaPlayer.stop();
                isSoundPlaying = false;
                System.out.println("Sound stopped manually.");
            }
        }
        // تشغيل الصوت لمدة معينة (بالثواني)
        public void playSoundForDuration(String resourcePath, int durationInSeconds) {
            playSound(resourcePath);
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    stopSound();
                }
            }, durationInSeconds * 500); // تحويل الثواني إلى مللي ثانية
        }
    }
    private void resetGame() {
        health = 3;
        score1 = 0;
        y = (int) -0.8;
        x2 = maxWidth / 2;
        for (int i = 0; i < planeCount; i++) {        planeX[i] = maxWidth + (i * 100);
            planeY[i] = (int) (Math.random() * (maxHeight / 2));    }
        for (int i = 0; i < parachuteCount; i++) {        parachuteX[i] = (int) (Math.random() * maxWidth);
            parachuteY[i] = maxHeight;    }
        bullets.clear();    missiles.clear();
        explosions.clear();
        //     missileTimer = 0;
        animationIndex = 0;
        // 7. إيقاف أي صوت نشط    soundPlayer.stopSound();
        System.out.println("Game has been reset.");
    }
    private void drawBackground2(GL gl, int texture1, int texture2) {
        // عند الوصول إلى قيمة الـ score المطلوبة، تبدأ الحركة
        if (score >= switchScoreThreshold) {
            // تحريك الخلفية الأولى إلى اليسار
            translateX1 -= translationSpeed;
            // تحريك الخلفية الثانية إلى اليسار
            translateX2 -= translationSpeed;
            // إذا خرجت الصورة الأولى بالكامل من اليسار، أعد وضعها إلى اليمين
            if (translateX1 <= -1.0f) {
                translateX1 = translateX2 + 1.0f; // وضع الصورة الأولى بجانب الثانية
            }

            // إذا خرجت الصورة الثانية بالكامل من اليسار، أعد وضعها إلى اليمين
            if (translateX2 <= -1.0f) {
                translateX2 = translateX1 + 1.0f; // وضع الصورة الثانية بجانب الأولى
            }
        }
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture1);
        gl.glPushMatrix();
        gl.glTranslatef(translateX1, 0.0f, 0.0f);
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex2f(-1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex2f(1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex2f(1.0f, 1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex2f(-1.0f, 1.0f);
        gl.glEnd();
        gl.glPopMatrix();
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture2);
        gl.glPushMatrix();
        gl.glTranslatef(translateX2, 0.0f, 0.0f);
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex2f(-1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex2f(1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex2f(1.0f, 1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex2f(-1.0f, 1.0f);
        gl.glEnd();
        gl.glPopMatrix();
        gl.glDisable(GL.GL_BLEND);
    }
    private void renderText(String text, float x, float y, Color color) {
        textRenderer.beginRendering(width, height);
        textRenderer.setColor(color);
        textRenderer.draw(text, (int)((x + 1) * width / 2 - textRenderer.getBounds(text).getWidth() / 2),
                (int)((y + 1) * height / 2 - textRenderer.getBounds(text).getHeight() / 2));
        textRenderer.endRendering();
    }
    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {}
    @Override
    public void displayChanged(GLAutoDrawable glAutoDrawable, boolean modeChanged, boolean deviceChanged) {}
    private void selectMenuOption(int index) {
        for (int i = 0; i < selected.length; i++) {
            selected[i] = (i == index); // Highlight selected option
        }
        switch (index) {
            case 0:
                System.out.println("1 Player ");
                currentScreen = "game";
                userName = JOptionPane.showInputDialog(null, "Enter your username:", "Username Input", JOptionPane.QUESTION_MESSAGE);
                break;
            case 1:
                System.out.println("2 Players ");
                currentScreen = "game2";
                userName = JOptionPane.showInputDialog(null, "Enter your username:", "Username Input", JOptionPane.QUESTION_MESSAGE);
                userName1 = JOptionPane.showInputDialog(null, "Enter your username:", "Username Input", JOptionPane.QUESTION_MESSAGE);
                break;
            case 2:
                System.out.println("help");
                currentScreen = "help";
                break;
            case 3:
                System.out.println("High score");
                currentScreen = "selected";
                break;
        }
    }
    private void selectMenuOption2(int index) {
        for (int i = 0; i < selected.length; i++) {
            selected[i] = (i == index); // Highlight selected option
        }
        switch (index) {
            case 0:
                System.out.println("Easy");
                currentscreen2 = "Easy game";
                currentScreen = "game";
                break;
            case 1:
                System.out.println("Medium");
                currentscreen2 = "medium game";
                currentScreen = "game";
                break;
            case 2:
                System.out.println("Hard");
                currentscreen2 = "Hard game";
                currentScreen = "game";
                break;
            case 3:
                System.out.println("1 V 1x");
                currentscreen2 = "1 V 1game2";
                currentScreen = "game2";
                break;
            case 4:
                System.out.println("1 V Computerx");
                currentscreen2 = "1 V Computergame2";
                currentScreen = "game2";
                break;
        }
    }
    public void handleKeyPress() {
        if (isKeyPressed(KeyEvent.VK_LEFT)) {
            if (x > 0) {
                x--;
            }
            animationIndex++;
        }
        if (isKeyPressed(KeyEvent.VK_RIGHT)) {
            if (x < maxWidth - 10) {
                x++;
            }
            animationIndex++;
        }
        if (isKeyPressed(KeyEvent.VK_SPACE)) {
            if (bullets.size() < 3) {
                Bullet newBullet = new Bullet(x, y,1);
                bullets.add(newBullet);
            }
        }
        if (isKeyPressed(KeyEvent.VK_A)) {
            if (x2 > 0) {
                x2--;
            }
            animationIndex++;
        }
        if (isKeyPressed(KeyEvent.VK_D)) {
            if (x2 < maxWidth - 10) {
                x2++;
            }
            animationIndex++;
        }
        if (isKeyPressed(KeyEvent.VK_F)) {
            if (bullets.size() < 3) {
                Bullet newBullet = new Bullet(x2, y2,2);
                bullets.add(newBullet);
            }
        }
    } public void handleKeyPress1(){
        if (isKeyPressed(KeyEvent.VK_ESCAPE)) {
            if(health<=0){
                currentScreen="menu";
            }
        }
    }
    public void handleKeyPress2(){
        if (isKeyPressed(KeyEvent.VK_ESCAPE)) {
            if(currentScreen.equals("game2")){
                currentScreen="menu";
        }
        }
    }
    public void handleKeyPress3(){
        if (isKeyPressed(KeyEvent.VK_ESCAPE)) {
            if(currentScreen.equals("help")){
                currentScreen="menu";
            }
        }
    }
    public void handleKeyPress4(){
        if (isKeyPressed(KeyEvent.VK_ESCAPE)) {
            if(currentScreen.equals("game")){
                currentScreen="menu";
            }
        }
    }
    public void mouseClicked(MouseEvent e) {
        float mouseX = (float) ((e.getX() / (float) width) * 2 - 1);
        float mouseY = (float) -((e.getY() / (float) height) * 2 - 1);
        float buttonStartY = 0.4f;
        float buttonSpacing = 0.2f;
        float buttonWidth = 0.8f;
        float buttonHeight = 0.12f;
        if (currentScreen.equals("menu")) {
            if (mouseX >= -buttonWidth / 2 && mouseX <= buttonWidth / 2) { // عرضياً يجب أن تكون داخل النصوص
                if (mouseY <= buttonStartY && mouseY >= buttonStartY - buttonHeight) {
                    selectMenuOption(0); // اختر "1 Player"
                } else if (mouseY <= buttonStartY - buttonSpacing && mouseY >= buttonStartY - buttonSpacing - buttonHeight) {
                    selectMenuOption(1); // اختر "2 Players"
                } else if (mouseY <= buttonStartY - 2 * buttonSpacing && mouseY >= buttonStartY - 2 * buttonSpacing - buttonHeight) {
                    selectMenuOption(2); // اختر "Help"
                } else if (mouseY <= buttonStartY - 3 * buttonSpacing && mouseY >= buttonStartY - 3 * buttonSpacing - buttonHeight) {
                    selectMenuOption(3); // اختر "High Score"
                }
            }
        }
        // تحقق من إذا ما كانت النقرة داخل أي من المناطق المحددة لواجهة الصعوبة
        else if (currentScreen.equals("game")) {
            if (mouseX >= -buttonWidth / 2 && mouseX <= buttonWidth / 2) { // عرضياً يجب أن تكون داخل النصوص
                if (mouseY <= buttonStartY && mouseY >= buttonStartY - buttonHeight) {
                    selectMenuOption2(0); // اختر "Easy"
                } else if (mouseY <= buttonStartY - buttonSpacing && mouseY >= buttonStartY - buttonSpacing - buttonHeight) {
                    selectMenuOption2(1); // اختر "Medium"
                } else if (mouseY <= buttonStartY - 2 * buttonSpacing && mouseY >= buttonStartY - 2 * buttonSpacing - buttonHeight) {
                    selectMenuOption2(2); // اختر "Hard"
                }
            }
        }
        else if (currentScreen.equals("game2")) {
            if (mouseX >= -buttonWidth / 2 && mouseX <= buttonWidth / 2) { // عرضياً يجب أن تكون داخل النصوص
                if (mouseY <= buttonStartY && mouseY >= buttonStartY - buttonHeight) {
                    selectMenuOption2(3); // اختر "Easy"
                } else if (mouseY <= buttonStartY - buttonSpacing && mouseY >= buttonStartY - buttonSpacing - buttonHeight) {
                    selectMenuOption2(4); // اختر "Medium"
                }
                }
            }
        if (e.getButton() == MouseEvent.BUTTON1) {
            isPaused = !isPaused;
        }
    }
    @Override
    public void mouseDragged(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e){}
    // فئة لتمثيل الطلقات
    class Bullet {
        public int x2,y2;
        int x, y;
        int tankId;
        Bullet(int startX, int startY) {
            x = startX;
            y = startY;
            x2=startX;
            y2=startY;
        }
        Bullet(int x, int y, int tankId) {
            this.x=x;
            this.x2 = x;
            this.y2 = y;
            this.tankId = tankId;
        }
    }
    // فئة لتمثيل الانفجارات
    class Explosion {
        int x, y, timeLeft;

        Explosion(int startX, int startY) {
            x = startX;
            y = startY;
            timeLeft = 10; // عدد الإطارات التي ستظل فيها الصورة ظاهرة
        }
    }
}

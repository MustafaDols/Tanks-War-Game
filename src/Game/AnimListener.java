package Game;
import java.awt.event.KeyListener;
import javax.media.opengl.GLEventListener;
public abstract class AnimListener implements GLEventListener, KeyListener {
    protected String assetsFolderName = "src//Assets//";

}
/*
 drawBackground2(gl,textureNames.length-4,textureNames.length-4);

            renderText("Score:"+String.valueOf(score), -0.8f, 0.6f, Color.WHITE);

            renderText("Score:"+String.valueOf(score1), 0.8f, 0.6f, Color.WHITE);
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
            for (int i = 0; i < planeCount; i++) {
                planeX[i] -= (constantSpeed-1.5); // استخدام السرعة الثابتة
                if (planeX[i] < -50) {
                    planeX[i] = maxWidth;
                    planeY[i] = (int) (Math.random() * (maxHeight) + (maxHeight / 2));
                    if (health <= 0) {
                        return;
                    }
                }
                DrawSprite2(gl, (int) planeX[i], (int) planeY[i], textureNames.length - 9, 2);
            }
            // تحريك البرشوت
            for (int i = 0; i < parachuteCount; i++) {

                parachuteY[i] -= (constantSpeed - 1.5); // استخدام السرعة الثابتة
                if (parachuteY[i] <= 0) {
                    parachuteX[i] = (int) (Math.random() * maxWidth);
                    parachuteY[i] = maxHeight;
//                    health--; // تقليل الصحة عند وصول البرشوت للأرض
                    if (health <= 0) {
                        return;
                    }
                }
                DrawSprite2(gl, (int) parachuteX[i], (int) parachuteY[i], 12, 2);
            }
            // رسم باقي العناصر والطلقات
            for (int i = 0; i < bullets.size(); i++) {
                Bullet bullet = bullets.get(i);
                bullet.y += ninjaStarSpeed;
                DrawSprite2(gl, bullet.x, bullet.y, 11, 0.5f);

                // التحقق من الاصطدام بالجنود player 1
                for (int j = 0; j < parachuteCount; j++) {
                    double dist = sqrdDistance(bullet.x, bullet.y, (int) parachuteX[j], (int) parachuteY[j]);
                    if (dist <= 50) {
                        System.out.println("Player 1 Hit Parachute!");
                        bullets.remove(i);
                        explosions.add(new Explosion((int) parachuteX[j], (int) parachuteY[j]));
                        soundPlayer.playSoundForDuration("explosion-42132.mp3",1);
                        parachuteX[j] = (int) (Math.random() * maxWidth);
                        parachuteY[j] = maxHeight;
                        if (bullet.tankId == 1) {
                            score += 10;
                        } else if (bullet.tankId == 2) {
                            score1 += 10;
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
                        soundPlayer.playSoundForDuration("explosion-42132.mp3",1);
                        planeX[j] = maxWidth;
                        planeY[j] = (int) (Math.random() * (maxHeight) + (maxHeight / 2));
                        planeSpeed[j] = (float) (Math.random() * 2 + 0.5); // تقليل سرعة الطائرة
                        if (bullet.tankId == 1) {
                            score += 20;
                        } else if (bullet.tankId == 2) {
                            score1 += 20;
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
                soundPlayer.playSoundForDuration("explosion-42132.mp3",1);
                if (explosion.timeLeft <= 0) {
                    explosions.remove(i);
                }
            }
 */
//renderText(userName, -0.8f, 0.8f, Color.WHITE);
//renderText(userName1, 0.8f, 0.8f, Color.WHITE);
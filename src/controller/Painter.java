package controller;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

public class Painter {
    /**
     * 设置 Graphics Context 围绕某一个点进行旋转的转换
     *
     * @param gc 应用 transform 的对象
     * @param angle 旋转的角度
     * @param px 旋转轴的x坐标
     * @param py 旋转轴的y坐标
     */
    private static void rotate(GraphicsContext gc, double angle, double px, double py) {
        Rotate r = new Rotate(angle, px, py);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

//    /**
//     * Draws an image on a graphics context.
//     *
//     * The image is drawn at (tlpx, tlpy) rotated by angle pivoted around the point:
//     *   (tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2)
//     *
//     * @param gc the graphics context the image is to be drawn on.
//     * @param angle the angle of rotation.
//     * @param tlpx the top left x co-ordinate where the image will be plotted (in canvas co-ordinates).
//     * @param tlpy the top left y co-ordinate where the image will be plotted (in canvas co-ordinates).
//     */
//    private static void drawRotatedImage(GraphicsContext gc, Image image, double angle, double tlpx, double tlpy) {
//        gc.save(); // saves the current state on stack, including the current transform
//        rotate(gc, angle, tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2);
//        gc.drawImage(image, tlpx, tlpy);
//        gc.restore(); // back to original state (before rotation)
//    }

}

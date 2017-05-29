
import java.io.File;
import java.util.function.Function;

public class Test {

    static String path = new File("").getAbsolutePath();

    public static long test(int max, String methodName, String fileName, Function<Object, PPMImage> f)
            throws Exception {
        long start, end, sum;
        sum = 0;
        for (int n = 0; n < max; n++) {
            start = System.currentTimeMillis();
            PPMImage image = f.apply(null);
            sum += (System.currentTimeMillis() - start);
            if(n == (max - 1)) {
                image.toFile(path + "/act/" + fileName);
            }
        }
        System.err.printf("%4d x %-20s -> %-21s took %4d ms\n", max, methodName, fileName, sum);
        return sum;
    }

    public static void main(String[] args) {

        
        try {
            // Minimalistic Tests
            RGB[] data = new RGB[121];
            for(int i = 0; i < data.length; i++) {
                data[i] =  new RGB(255, 255, 255);
            }
            data[5*11 + 7] = new RGB(255, 0, 0);
            data[5*11 + 2] = new RGB(0, 0, 255);
            PPMImage testImage = new PPMImage(11, 11, 255, data);
        
            testImage.toFile(path + "/min/testImage.ppm");
            testImage.greyscale().toFile(path + "/min/testImageGrey.ppm");
            testImage.gaussianBlur(3, 3.0).toFile(path + "/min/testImageBlur.ppm");
            testImage.mirrorImage().toFile(path + "/min/testImageMirror.ppm");
            testImage.mirrorImage2().toFile(path + "/min/testImageMirror2.ppm");
            testImage.negate().toFile(path + "/min/testImageNeg.ppm");

            // Florence Tests / Performance Tests
            PPMImage image = new PPMImage(path + "/act/florence.ppm");
            long sum = 0;
            System.err.printf("=================================================================\n");
            sum += test(499, "greyscale()", "florenceGrey.ppm", (o) -> image.greyscale());
            sum += test(2, "gaussianBlur(20, 3)", "florenceBlur.ppm", (o) -> image.gaussianBlur(20, 3));
            sum += test(499, "mirrorImage()", "florenceMirror.ppm", (o) -> image.mirrorImage());
            sum += test(499, "mirrorImage2()", "florenceMirror2.ppm", (o) -> image.mirrorImage2());
            sum += test(499, "negate()", "florenceNeg.ppm", (o) -> image.negate());
            System.err.printf("=================================================================\n");
            System.err.printf("       %-20s -> %-21s took %4d ms\n", "total", "files", sum);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

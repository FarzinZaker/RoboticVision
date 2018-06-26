package vision

import grails.transaction.Transactional
import groovy.time.TimeCategory
import org.tensorflow.Graph
import org.tensorflow.Session
import org.tensorflow.Tensor
import sun.misc.GC

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Transactional
class RecognitionService {

    def modelPath = '/Personal/DevDesk/Vision/models/inception_dec_2015'

    byte[] graphDefinition
    List<String> labels

    private String lastDetectedObject = null

    private init() {
        if (!graphDefinition || !labels) {
            graphDefinition = readAllBytesOrExit(Paths.get(modelPath, "tensorflow_inception_graph.pb"))
            labels = readAllLinesOrExit(Paths.get(modelPath, "imagenet_comp_graph_label_strings.txt"))
        }
    }

    String doIt(String path) {

        init()

        byte[] imageBytes = readAllBytesOrExit(Paths.get(path))

        Tensor image = Tensor.create(imageBytes)
        float[] labelProbabilities = executeInceptionGraph(graphDefinition, image)
        int bestLabelIdx = maxIndex(labelProbabilities)
//        System.out.println(
//                String.format(
//                        "BEST MATCH: %s (%.2f%% likely)",
//                        labels.get(bestLabelIdx), labelProbabilities[bestLabelIdx] * 100f))

        def newObject = labels.get(bestLabelIdx)
        if (!['dummy', 'nipple'].contains(newObject?.toLowerCase()?.trim()))
            lastDetectedObject = newObject
        return String.format(
                "BEST MATCH: %s (%.2f%% likely)",
                labels.get(bestLabelIdx), labelProbabilities[bestLabelIdx] * 100f)

    }

    String getLastDetectedObject() {
        lastDetectedObject
    }

    private static float[] executeInceptionGraph(byte[] graphDefinition, Tensor image) {
        try {
            GC.collect()
            Graph g = new Graph()
            g.importGraphDef(graphDefinition)
            try {
                Session s = new Session(g)
                Tensor result = s.runner().feed("DecodeJpeg/contents", image).fetch("softmax").run().get(0)
                final long[] rshape = result.shape()
                if (result.numDimensions() != 2 || rshape[0] != 1) {
                    throw new RuntimeException(
                            String.format(
                                    "Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
                                    Arrays.toString(rshape)))
                }
                int nlabels = (int) rshape[1]

                return result.copyTo(new float[1][nlabels])[0]
            }
            catch (ignore) {
                return []
            }
        }
        catch (ignore) {
            return []
        }
    }

    private int maxIndex(float[] probabilities) {

        for (int i = 1; i < probabilities.length; ++i) {
            if (probabilities[i] > 0.01) {
                System.out.println(
                        String.format(
                                "BEST MATCH: %s (%.2f%% likely)",
                                labels.get(i), probabilities[i] * 100f))
            }
        }

        int best = 0
        for (int i = 1; i < probabilities.length; ++i) {
            if (probabilities[i] > probabilities[best]) {
                best = i
            }
        }

        return best
    }

    private static byte[] readAllBytesOrExit(Path path) {
        try {
            return Files.readAllBytes(path)
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: " + e.getMessage())
            System.exit(1)
        }
        return null
    }

    private static List<String> readAllLinesOrExit(Path path) {
        try {
            return Files.readAllLines(path, Charset.forName("UTF-8"))
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: " + e.getMessage())
            System.exit(0)
        }
        return null
    }
}

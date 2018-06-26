package vision

class DetectController {

    def recognitionService

    def index() {
        [detectedObject: recognitionService.lastDetectedObject]
    }

    def changed() {
        render recognitionService.lastDetectedObject
    }
}

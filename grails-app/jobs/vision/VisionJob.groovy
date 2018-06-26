package vision

class VisionJob {

    static concurrent = false
    static triggers = {
        simple repeatInterval: 3000l // execute job once in 5 seconds
    }

    def recognitionService

    def execute() {

        try {
            def files = new File('/Personal/DevDesk/Vision/stream/').listFiles()?.sort { -it.lastModified() }
            File file = files.find { it.name.endsWith("jpg") }
            for (def i = 0; i < files.size(); i++)
                if (!file || files[i].name != file.name)
                    files[i].delete()
            recognitionService.doIt(file.absolutePath)
        } catch (ignore) {
        }
    }
}

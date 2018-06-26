package vision

import grails.transaction.Transactional

@Transactional
class StreamService {


    def path = '/Personal/DevDesk/Vision/stream/'
    def ip = '192.168.0.103'

    def split() {
        def process = "/Applications/VLC.app/Contents/MacOS/VLC rtsp://${ip}:8554/ --video-filter scene --scene-format jpg --scene-prefix test --scene-path ${path} --no-scene-replace --scene-ratio 1".execute()
        process.waitFor()
    }
}

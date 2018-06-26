package vision

class BootStrap {

    def streamService

    def init = { servletContext ->
        streamService.split()
    }
    def destroy = {
    }
}

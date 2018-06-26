<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title id="title">${detectedObject}</title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>
</head>

<body>

<div id="content" role="main">
    <section class="row colset-2-its">
        <iframe id="loader" frameborder="0" src="https://www.bing.com/images/search?q=${URLEncoder.encode(detectedObject ?: ' ', 'UTF-8')}" width="100%" height="1000px"
                style="margin-top:-190px;"></iframe>
    </section>
</div>
<script language="javascript">
    var detectedObject = '${detectedObject}';
    setInterval(function () {
        $.ajax({
            url: "${createLink(action: 'changed', id:detectedObject)}"
        }).done(function (data) {
            if (detectedObject != data) {
                detectedObject = data;
                $('#header').html(detectedObject);
                document.title = detectedObject;
                $('#loader').attr('src', "https://www.bing.com/images/search?q=" + encodeURIComponent(detectedObject));
            }
        });
    }, 5000)
</script>
</body>
</html>

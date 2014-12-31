$.contextMenu({
    selector: '.right-click-edit', 
    build: ($trigger, e) ->
        console.log($trigger[0])
        console.log($trigger)
        console.log(e)
        # this callback is executed every time the menu is to be shown
        # its results are destroyed every time the menu is hidden
        # e is the original contextmenu event, containing e.pageX and e.pageY (amongst other data)
        return {
            callback: (key, options) ->
                m = "clicked: " + key
                window.console && console.log(m) || alert(m)
            items: {
                "edit": {name: "Change Image", icon: "edit"}
            }
        }

})

class AdminViewModel
    constructor: ->
        @pages = ko.observableArray()
        
        @_requestPages()

    _requestPages: =>
        callback = (data) =>
            console.log(data)
            @pages(data.pages)
            #@header({ text: data.header })
            #@layoutItems(data.widgets)
        $.getJSON("/pages", callback)


ready = ->
    ko.applyBindings(new AdminViewModel())

$(document).ready(ready)
$(document).on('page:load', ready)

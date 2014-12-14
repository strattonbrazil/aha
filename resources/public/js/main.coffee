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

class EditViewModel
    constructor: ->
        @headerText = 'HEADER'

ready = ->
    ko.applyBindings(new EditViewModel())
    console.log('starting knockout')

$(document).ready(ready)
$(document).on('page:load', ready)

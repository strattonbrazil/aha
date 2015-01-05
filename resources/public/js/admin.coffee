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
        #@pageTypes = ko.observableArray(["gallery", "link"])
        @menus = ko.observableArray()
        #@availableMenus = ko.computed(=> ["(unassigned)"].concat(@menus()))

        @_requestPages()

    _requestPages: =>
        parseResponse = (data) =>
            @menus(data.menus)
            @pages(data.pages)

        $.getJSON("/pages", parseResponse)

    editPage: (page={}, event) =>
        if page.label?
            pageName = page.label
        else
            pageName = 'new_page'
        editDialog = $('#edit-page-dialog')
        nameInput = $('#edit-page-name-input')

        closedCallback = =>
            newName = nameInput.val()
            if newName isnt ''
                if page.id? # editing page
                    if pageName isnt newName # changed
                        @_updatePageName(page.id, newName)
                else # new page
                    @_createPage(newName)

        editDialog.one('closed', closedCallback)
        
        editDialog.foundation('reveal', 'open')
        
    # loads a menu or creates a new one
    editMenu: (menu={}, event) =>
        if menu.label?
            menuName = menu.label
        else
            menuName = 'new_menu'
            
        editDialog = $('#edit-menu-dialog')
        
        #removeDialog = $('#remove-menu-dialog')
        #nameInput = $('#edit-menu-name-input')
        #removeButton = $('#edit-menu-remove-button')
        #confirmRemoveButton = $('#remove-menu-button')
        #openedCallback = ->


            
            
           
        editVM = {
            name: ko.observable(menuName)
            editMode: menu.id?
            remove: =>
                removeDialogElement = document.getElementById('remove-menu-dialog')
                removeDialog = $(removeDialogElement).dialog({
                    title: 'Delete Menu'
                    modal: true
                    close: ->
                        ko.cleanNode(removeDialogElement)
                })
        
                removeVM = {
                    name: menuName
                    remove: =>
                        @_removeMenu(menu.id)
                        removeDialog.dialog('close')
                        editDialog.dialog('close')
                }
                ko.applyBindings(removeVM, removeDialogElement)
        }

        if menu.id?
            title = 'Edit Menu'
        else
            title = 'New Menu'

        dialogElement = document.getElementById('edit-menu-dialog')
        ko.applyBindings(editVM, dialogElement)
        $(dialogElement).dialog({
            title: title
            modal: true
            close: =>
                if menu.id? # updating a menu
                    if editVM.name() isnt menuName
                        @_updateMenuName(menu.id, editVM.name())
                else
                    @_createMenu(editVM.name())
                
                ko.cleanNode(dialogElement)
        })

    _updateMenuName: (id, name) ->
        $.ajax({
          type: "PATCH"
          url: "/menus/#{id}"
          data: { label: name }
          success: @_requestPages
        })

    _removeMenu: (id) ->
        $.ajax({
            type: "DELETE"
            url: "/menus/#{id}"
            success: @_requestPages
        })
                    
    newMenu: =>
        @editMenu()

    _createMenu: (name) =>
        $.post("/menus", { label: name }, @_requestPages)

    newPage: =>
        @editPage()

    _createPage: (name) =>
        $.post("/pages", { label: name }, @_requestPages)

ready = ->
    ko.applyBindings(new AdminViewModel(), document.getElementById('main-content'))

$(document).ready(ready)
$(document).on('page:load', ready)

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

class Menu
    constructor: (adminVM, menuData) ->
        @_adminVM = adminVM
        @id = menuData.id
        @name = menuData.title

        @pages = ko.observableArray(menuData.pages)
        @pages.menuId = @id
        @orderChanged = (movedInfo) =>
            pageId = movedInfo.item.id
            @_movePage(pageId,
                movedInfo.sourceParent.menuId, movedInfo.sourceIndex,
                movedInfo.targetParent.menuId, movedInfo.targetIndex)

    _movePage: (pageId, srcMenuId, srcIndex, targetMenuId, targetIndex) ->
        $.ajax({
            type: "PATCH"
            url: "/pages/#{pageId}/order"
            data: {
                srcMenuId: srcMenuId,
                srcIndex: srcIndex,
                targetMenuId: targetMenuId,
                targetIndex: targetIndex
            }
            success: -> console.log('moved')
        })

    newPage: (viewmodel, event) =>
        @editPage()

    editPage: (page={}, event) =>
        if page.title?
            pageName = page.title
        else
            pageName = 'new_page'
            
        editDialog = $('#edit-page-dialog')

        editVM = {
            name: ko.observable(pageName)
            editMode: page.id?
            remove: =>
                removeDialogElement = document.getElementById('remove-dialog')
                removeDialog = $(removeDialogElement).dialog({
                    title: 'Delete Page'
                    modal: true
                    close: ->
                        ko.cleanNode(removeDialogElement)
                })

                removeVM = {
                    name: pageName
                    remove: =>
                        @_removePage(page.id)
                        removeDialog.dialog('close')
                        editDialog.dialog('close')
                }
                ko.applyBindings(removeVM, removeDialogElement)
        }

        if page.id?
            title = 'Edit Page'
        else
            title = 'New Page'

        dialogElement = document.getElementById('edit-page-dialog')
        ko.applyBindings(editVM, dialogElement)
        $(dialogElement).dialog({
            title: title
            modal: true
            close: =>
                if page.id? # updating page
                    if editVM.name() isnt pageName
                        @_updatePageName(page.id, editVM.name())
                else
                    @_createPage(editVM.name())

                ko.cleanNode(dialogElement)
        })

    _createPage: (name) =>
        $.post("/pages", { title: name, menuId: @id }, @_adminVM._requestPages)

class AdminViewModel
    constructor: ->
        @menus = ko.observableArray()
        #@pages = ko.observableArray()

        @_requestPages()

    _requestPages: =>
        parseResponse = (data) =>
            @menus.removeAll()
            for menuData in data.menus
                @menus.push(new Menu(@, menuData))
            #@pages(data.pages)

        $.getJSON("/menus", parseResponse)


        
    # loads a menu or creates a new one
    editMenu: (menu={}, event) =>
        if menu.title?
            menuName = menu.title
        else
            menuName = 'new_menu'
            
        editDialog = $('#edit-menu-dialog')
        
        editVM = {
            name: ko.observable(menuName)
            editMode: menu.id?
            remove: =>
                removeDialogElement = document.getElementById('remove-dialog')
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
          data: { title: name }
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
        $.post("/menus", { title: name }, @_requestPages)

    newPage: =>
        @editPage()

    _createPage: (name) =>
        $.post("/pages", { title: name }, @_requestPages)

ready = ->
    ko.applyBindings(new AdminViewModel(), document.getElementById('main-content'))

$(document).ready(ready)
$(document).on('page:load', ready)

// Generated by CoffeeScript 1.4.0
(function() {
  var AdminViewModel, ready,
    __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; };

  $.contextMenu({
    selector: '.right-click-edit',
    build: function($trigger, e) {
      console.log($trigger[0]);
      console.log($trigger);
      console.log(e);
      return {
        callback: function(key, options) {
          var m;
          m = "clicked: " + key;
          return window.console && console.log(m) || alert(m);
        },
        items: {
          "edit": {
            name: "Change Image",
            icon: "edit"
          }
        }
      };
    }
  });

  AdminViewModel = (function() {

    function AdminViewModel() {
      this._requestPages = __bind(this._requestPages, this);
      this.pages = ko.observableArray();
      this._requestPages();
    }

    AdminViewModel.prototype._requestPages = function() {
      var callback,
        _this = this;
      callback = function(data) {
        console.log(data);
        return _this.pages(data.pages);
      };
      return $.getJSON("/pages", callback);
    };

    return AdminViewModel;

  })();

  ready = function() {
    return ko.applyBindings(new AdminViewModel());
  };

  $(document).ready(ready);

  $(document).on('page:load', ready);

}).call(this);

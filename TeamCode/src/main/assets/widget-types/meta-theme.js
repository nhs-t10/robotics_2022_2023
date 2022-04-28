module.exports = {
    init: function (parent) {
        var select = document.createElement("select");
        var themes = ["dark", "light"];
        var current = localStorage.getItem("theme");

        for (var i = 0; i < themes.length; i++) {
            var opt = document.createElement("option");
            if (themes[i] == current) opt.selected = true;
            opt.textContent = themes[i];
            select.appendChild(opt);
        }

        select.addEventListener("change", function () {
            localStorage.setItem("theme", themes[select.selectedIndex]);
            document.body.classList.remove("dark-theme");
            document.body.classList.remove("light-theme");
            document.body.classList.add(themes[select.selectedIndex] + "-theme");
        });

        parent.appendChild(select);
    },
    reset: function () { },
    ondata: function (data, parent) { }
}
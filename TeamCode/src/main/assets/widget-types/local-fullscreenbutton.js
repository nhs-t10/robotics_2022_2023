module.exports = {
    init: function(parent) {
        var button = document.createElement("button");
        button.setAttribute("style", `height:100%;border:0;margin:0.5em;background:var(--widget-settings-color)`);
        button.addEventListener("click", function() {
            if (document.fullscreenElement) {
                document.exitFullscreen();
                button.style.background = `var(--widget-settings-color)`;
            } else {
                document.documentElement.requestFullscreen();
                button.style.background = `var(--close-widget-color)`;
            }
        });
        parent.appendChild(button);
    },
    reset: function() {},
    ondata: function() {}
}
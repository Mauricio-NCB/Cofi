document.addEventListener('DOMContentLoaded', function () {

    var calendarEl = document.getElementById('calendar');

    var calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'es',
        height: 'auto',
        dayMaxEvents: true,
        displayEventTime: false,

        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },

        events: '/eventos/api/mis-eventos',

        eventDidMount: function(info) {

            let estado = info.event.extendedProps.estado;

            if (estado === 'en_curso') info.el.style.backgroundColor = '#198754';
            if (estado === 'proximo') info.el.style.backgroundColor = '#ffc107';
            if (estado === 'terminado') info.el.style.backgroundColor = '#6c757d';
            if (estado === 'cancelado') info.el.style.backgroundColor = '#dc3545';
        },

        eventClick: function(info) {
            alert("Evento: " + info.event.title);
        }
    });

    calendar.render();
});

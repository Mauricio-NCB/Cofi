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

        buttonText: {
            today: 'Hoy',
            month: 'Mes',
            week: 'Semana',
            day: 'Día'
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

            const event = info.event;

            document.getElementById("modalEventTitle").textContent = event.title;
            document.getElementById("modalEventDesc").textContent = event.extendedProps.description;
            const fecha = new Date(event.start);
            document.getElementById("modalDate").textContent = fecha.toLocaleDateString('es-ES');
            document.getElementById("modalTime").textContent = fecha.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
            document.getElementById("modalCapacity").textContent = event.extendedProps.maxCapacity;

            const modal = new bootstrap.Modal(document.getElementById('viewEventModal'));
            modal.show();
        }
    });

    calendar.render();
});

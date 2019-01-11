var ctx = document.getElementById("myChart");

document.body.width = window.innerWidth - 1;
document.body.height = window.innerWidth - 10;
ctx.width  = window.innerWidth - 1;
ctx.height = window.innerHeight - 10;

var myDoughnutChart = new Chart(ctx, {
    type: 'doughnut',
    data: {
        datasets: [{
            data: [10, 20, 30, 5, 15],
            backgroundColor: ["#3e95cd", "#8e5ea2","#3cba9f","#e8c3b9","#c45850"]
        }],
        labels: [
            'Red',
            'Yellow',
            'Blue',
            'Green',
            'Purple'
        ]
    },
    options: {
        responsive: false,
        maintainAspectRatio: false,
    }
});
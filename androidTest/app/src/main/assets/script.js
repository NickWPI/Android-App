function httpGetAsync(theUrl, callback) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
            callback(xmlHttp.responseText);
    }
    xmlHttp.open("GET", theUrl, true); // true for asynchronous 
    xmlHttp.send(null);
}

function processJSONData(url, onEnd) {
    let priceList = [];
    
    httpGetAsync(url, (resp) => {
        let jsonObj = JSON.parse(resp);
        let jsonData = jsonObj["Data"];
        let prevData = -1;
        for(let i = 0; i < jsonData.length; i++) {
            let dataObj = jsonData[i];
            let price = dataObj["close"];
            var data = {
                price : price,
                volume : dataObj["volumefrom"],
                isPositiveChange : (price >= prevData ? true : false)
            };
            //let price = dataObj["close"];
            prevData = data.price;
            //priceList.push(price);
            priceList.push(data);
        }

        onEnd(priceList);
    });
}

function getHistoricalMinuteCryptoPrice(fromCurrency, market, limit, onEnd) {
    let url = "https://min-api.cryptocompare.com/data/histominute?fsym=" + fromCurrency
        + "&tsym=" + market + "&limit=" + limit + "&aggregate=" + 3;
    processJSONData(url, onEnd);
}

function getHistoricalHourlyCryptoPrice(fromCurrency, market, limit, onEnd) {
    let url = "https://min-api.cryptocompare.com/data/histohour?fsym=" + fromCurrency
        + "&tsym=" + market + "&limit=" + limit + "&aggregate=" + 3;
    processJSONData(url, onEnd);
}

function getHistoricalDailyCryptoPrice(fromCurrency, market, limit, onEnd) {
    let url = "https://min-api.cryptocompare.com/data/histoday?fsym=" + fromCurrency
        + "&tsym=" + market + "&limit=" + limit + "&aggregate=" + 3;
    processJSONData(url, onEnd);
}

var ctx = document.getElementById("myChart");
var myChart = {};
var maxBarDataCount = 300;

var currentCurrency = Android.getCurrentCryptocurrency();

var pair = currentCurrency.split("-");
if(pair.length == 1) {
    pair.push("USD");
}

//for some reason, adding -1 fixes the going out of boundaries thing
document.body.width = window.innerWidth - 1;
document.body.height = window.innerWidth - 40;
ctx.width  = window.innerWidth - 1;
ctx.height = window.innerHeight - 40;
/*ctx.style.width = '800px';
ctx.style.height = '500px';*/

getHistoricalMinuteCryptoPrice(pair[0], pair[1], "60", (priceList) => {
    let xAxis = [];
    for(let i = 0; i < priceList.length; i++) {
        xAxis.push("");
        //console.log(priceList[i]);
    }
    let prices = priceList.map(function(value, index, array) {
        return value.price;
    });
    let volumes = priceList.map(function(value, index, array) {
        return value.volume;
    });
    let maxVolume = Math.max.apply(null, volumes);
    let volumeColors = priceList.map(function(value, index, array) {
        if(!value.isPositiveChange) {
            return "rgba(216, 27, 96, 0.3)";
        } else {
            return "rgba(0, 200, 83, 0.3)";
        }
    });

    myChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: xAxis,
            datasets: [{
                type: 'line',
                data: prices,
                label: "",
                //xAxisID: 'Ax',
                yAxisID: 'Ay',
                borderColor: "#3e95cd",
                fillColor: "rgba(252,147,65,0.5)",
            },
            {
                type: 'bar',
                data: volumes,
                label: "",
                //xAxisID: 'Bx',
                yAxisID: 'By',
                backgroundColor: volumeColors
            }]
        },
        options: {
            responsive: false,
            maintainAspectRatio: false,
            elements:{
                point:{
                    radius: 0
                }
            },
            legend: {
                display: false
            },
            tooltips: {
                callbacks: {
                   label: function(tooltipItem) {
                          return tooltipItem.yLabel;
                   }
                }
            },
            scales: {
                xAxes: [{
                    gridLines: {
                        color: "rgba(0, 0, 0, 0)",
                    }
                }],
                yAxes: [{
                    id: 'Ay',
                    type: 'linear',
                    position: 'left',
                }, {
                    id: 'By',
                    type: 'linear',
                    position: 'right',
                    display: false,
                    gridLines: {
                        color: "rgba(0, 0, 0, 0)",
                    },
                    ticks: {
                        max: maxVolume * 2
                    }
                }],
                /*yAxes: [{
                    gridLines: {
                        color: "rgba(0, 0, 0, 0)",
                    }
                }]*/
            }
        }
    });
});

function updateChart(priceList) {
    let prices = priceList.map(function(value, index, array) {
        return value.price;
    });
    let volumes = priceList.map(function(value, index, array) {
        return value.volume;
    });
    let isPositiveChanges = priceList.map(function(value, index, array) {
        return value.isPositiveChange;
    });
    let volumeColors = [];
    //this interval isn't super exact
    let interval = Math.ceil(volumes.length / maxBarDataCount);
    //data needs to be removed or else the chart wont render
    //chartJs currently doesn't support dual x labels for charts
    //they don't support "scatter" bar charts (specifying x coordinates
    //for bar data), so basically ChartJs sucks
    if(volumes.length > maxBarDataCount) {
        let priceFilterIndex = 0;
        let tempPrices = [];
        for(let i = 0; i < prices.length; i += interval) {
            tempPrices.push(prices[i]);
        }
        prices = tempPrices;
        let isPositiveChange = false;
        let compressedChanges = [];
        let compressedVolumes = [];
        for(let i = 0; i < volumes.length; i += interval) {
            let tempArray = volumes.slice(i, i + interval);
            let value = 0;
            for(let j = 0; j < tempArray.length; j++) {
                value += tempArray[j];
            }
            compressedVolumes.push(value);
            let averageResult = 0;
            for(let j = i; j < i + interval; j++) {
                let isPositiveChange = isPositiveChanges[j] ? 1 : -1;
                let volume = volumes[j];
                averageResult += (isPositiveChange * volume);
            }
            if(averageResult >= 0) {
                compressedChanges.push("rgba(0, 200, 83, 0.3)");
            }
            else {
                compressedChanges.push("rgba(216, 27, 96, 0.3)");
            }
        }
        volumes = compressedVolumes;
        volumeColors = compressedChanges;
    }
    else {
        volumeColors = priceList.map(function(value, index, array) {
            if(!value.isPositiveChange) {
                return "rgba(216, 27, 96, 0.3)";
            } else {
                return "rgba(0, 200, 83, 0.3)";
            }
        });
    }
    let maxVolume = Math.max.apply(null, volumes);

    let xAxis = [];
    for(let i = 0; i < prices.length; i++) {
        xAxis.push("");
    }

    /*var dataset1 = [{labels: xAxis}];
    for(let i = 0; i < prices.length; i++) {
        dataset1.push({x: i, y: prices[i]})
    }
    var dataset2 = [{labels: xAxis}];
    let labelIndex = 0;
    for(let i = 0; i < volumes.length; i++) {
        dataset2.push({x: labelIndex, y: volumes[i]})
        labelIndex += interval;
    }*/

    myChart.config.data.labels = xAxis;
    myChart.config.data.datasets =
    [
        {
            type: 'line',
            data: prices,
            label: "",
            yAxisID: 'Ay',
            borderColor: "#3e95cd",
            fillColor: "rgba(252,147,65,0.5)",
        },
        {
            type: 'bar',
            data: volumes,
            label: "",
            yAxisID: 'By',
            backgroundColor: volumeColors
        }
    ];
    myChart.config.options.scales = {
        xAxes: [{
            gridLines: {
                color: "rgba(0, 0, 0, 0)",
            }
        }],
        yAxes: [{
            id: 'Ay',
            type: 'linear',
            position: 'left',
        }, {
            id: 'By',
            type: 'linear',
            position: 'right',
            display: false,
            gridLines: {
                color: "rgba(0, 0, 0, 0)",
            },
            ticks: {
                max: maxVolume * 2
            }
        }],
    }

    myChart.update(0);
}

function update1hChart() {
    getHistoricalMinuteCryptoPrice(pair[0], pair[1], "60", (priceList) => {
        updateChart(priceList);
    });
}

function update24hChart() {
    getHistoricalMinuteCryptoPrice(pair[0], pair[1], "1440", (priceList) => {
        updateChart(priceList);
    });
}

function update7dChart() {
    getHistoricalHourlyCryptoPrice(pair[0], pair[1], "168", (priceList) => {
        updateChart(priceList);
    });
}

function update1mChart() {
    getHistoricalHourlyCryptoPrice(pair[0], pair[1], "672", (priceList) => {
        updateChart(priceList);
    });
}

function update3mChart() {
    getHistoricalDailyCryptoPrice(pair[0], pair[1], "90", (priceList) => {
        updateChart(priceList);
    });
}

function update1yChart() {
    getHistoricalDailyCryptoPrice(pair[0], pair[1], "365", (priceList) => {
        updateChart(priceList);
    });
}

function update5yChart() {
    getHistoricalDailyCryptoPrice(pair[0], pair[1], "1825", (priceList) => {
        updateChart(priceList);
    });
}

document.getElementById("1hButton").addEventListener("click", function() {
    update1hChart();
});

document.getElementById("24hButton").addEventListener("click", function() {
    update24hChart();
});

document.getElementById("7dButton").addEventListener("click", function() {
    update7dChart();
});

document.getElementById("1mButton").addEventListener("click", function() {
    update1mChart();
});

document.getElementById("3mButton").addEventListener("click", function() {
    update3mChart();
});

document.getElementById("1yButton").addEventListener("click", function() {
    update1yChart();
});

document.getElementById("5yButton").addEventListener("click", function() {
    update5yChart();
});

/*document.body.style.height = window.innerHeight +'px';

function autoResizeDiv()
{
    document.body.style.height = window.innerHeight +'px';
}
window.onresize = autoResizeDiv;*/
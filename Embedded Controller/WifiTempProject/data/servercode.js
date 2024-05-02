//! Download button
var downloadButton = document.getElementById('download-data');

//! Add event listener so when the button is clicked, the user is forwarded to the endpoint which initiates the download.
downloadButton.addEventListener('click', function() {
  window.location.href = "/download";
});

//! Instantiate chart
var chartT = new Highcharts.Chart({
  chart:{ renderTo : 'chart-temperature' },
  title: { text: 'Temperature' },
  series: [{
    showInLegend: false,
    data: []
  }],
  plotOptions: {
    line: { animation: false,
      dataLabels: { enabled: true }
    },
    series: { color: '#059e8a' }
  },
  xAxis: { type: 'datetime',
    dateTimeLabelFormats: { second: '%H:%M:%S' }
  },
  yAxis: {
    title: { text: 'Temperature (Celsius)' }
  },
  credits: { enabled: false }
});

//! Fetch the slider element:
var timeSlider = document.getElementById('time-slider');
var allTimestamps = []; // Store all timestamps initially
var allTemperatures = []; // Store all temperatures initially

// Function to update chart based on slider value
function updateChartData(hours) {
  var filteredTimestamps = [];
  var filteredTemperatures = [];

  // Assuming timestamps are in milliseconds
  var oneHour = 1000 * 60 * 60; // Milliseconds in an hour
  var currentTime = Date.now();
  var threshold = currentTime - (hours * oneHour);

  // Filter data based on slider value (last 'hours')
  allTimestamps.forEach(function(timestamp, index) {
    if (timestamp >= threshold) {
      filteredTimestamps.push(timestamp);
      filteredTemperatures.push(allTemperatures[index]);
    }
  });

  // Update chart data with filtered values
  chartT.series[0].setData(filteredTimestamps.map((timestamp, index) => ({ x: timestamp, y: filteredTemperatures[index] })));
}
updateChartData(24);

// Update chart on slider change
timeSlider.addEventListener('change', function() {
  var hours = parseInt(this.value);
  updateChartData(hours);
});

// Fetch data and store all values initially
function getTempHistory() {
  console.log("Getting history..");
  var xmlhttp = new XMLHttpRequest();
  xmlhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      // Process the received data
      var csvData = this.responseText;
      var lines = csvData.split("\n"); // Split data into lines

      // Skip the header line (assuming the first line contains labels)
      lines.shift(); // Remove the first element (header)

      // Parse each data line
      lines.forEach(function(line) {
        var values = line.split(","); // Split line into values
        var timestamp = Date.parse(values[1] + " " + values[2]); // Parse date+time to timestamp
        var temperature = parseFloat(values[3]); // Convert temperature to number

        allTimestamps.push(timestamp);
        allTemperatures.push(temperature);
      });

      // Update chart with all data initially
      updateChartData(24);
    }
  };
  xmlhttp.open("GET", "/history", true);
  xmlhttp.send();
}



// Call getTempHistory on page load 
getTempHistory();

setInterval(function ( ) {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      var x = (new Date()).getTime(),
          y = parseFloat(this.responseText);
      //console.log(this.responseText);
      if(chartT.series[0].data.length > 40) {
        chartT.series[0].addPoint([x, y], true, true, true);
      } else {
        chartT.series[0].addPoint([x, y], true, false, true);
      }
    }
  };
  xhttp.open("GET", "/temperature", true);
  xhttp.send();
}, 5000 ) ;


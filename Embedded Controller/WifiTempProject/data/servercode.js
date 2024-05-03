/**
 * @file
 * Contains the javascript to be run on the index page.
 */
//! Download button
var downloadButton = document.getElementById('download-data');

//! Add event listener so when the button is clicked, the user is forwarded to the endpoint which initiates the download.
downloadButton.addEventListener('click', function() {
  window.location.href = "/download";
});

//! Delete log button
var deleteButton = document.getElementById('delete-data');

//! Add event listener so endpoint for deleting log is used. Make sure chart is updated.
deleteButton.addEventListener('click', function() {
  // Send an AJAX request to the delete endpoint
  var xhr = new XMLHttpRequest();
  xhr.open('POST', '/deletelog', true);
  xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
  xhr.onload = function() {
    if (xhr.status === 200) {
      console.log('Log file deleted successfully');
    } else {
      console.error('Error deleting log file:', xhr.statusText);
    }
  };
  xhr.onerror = function() {
    console.error('Error contacting server (network error)');
  };
  updateChartData(24);
  xhr.send();
});

/**
 * @var
 * Defines and instantiates the chart on which temperatuers will be plotted.
 */
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

/**
 * @var
 * Fetches the slider element from the html file.
 */
var timeSlider = document.getElementById('time-slider');
var allTimestamps = []; //! Array to store all timestamps initially
var allTemperatures = []; //! Array to store all temperatures initially

/**
 * @function
 * Updates chart based on the selected value on the slider
 * @param {} hours 
 */
function updateChartData(hours) {
  var filteredTimestamps = [];
  var filteredTemperatures = [];

  // Assuming timestamps are in milliseconds
  var oneHour = 1000 * 60 * 60; // Milliseconds in an hour
  var currentTime = Date.now();
  var threshold = currentTime - (hours * oneHour);

  //! Filter data based on slider value (last 'hours')
  allTimestamps.forEach(function(timestamp, index) {
    if (timestamp >= threshold) {
      filteredTimestamps.push(timestamp);
      filteredTemperatures.push(allTemperatures[index]);
    }
  });

  //! Update chart data with filtered values
  chartT.series[0].setData(filteredTimestamps.map((timestamp, index) => ({ x: timestamp, y: filteredTemperatures[index] })));
}
//! Initiates the chart with the historical data, set to 24 hour range by default.
updateChartData(24);

//! Update chart on slider change
timeSlider.addEventListener('change', function() {
  var hours = parseInt(this.value);
  updateChartData(hours);
});

/**
 * Fetches and plots the entire logged temperature, within the limits of the hours selected via the slider.
 */
function getTempHistory() {
  console.log("Getting history..");
  var xmlhttp = new XMLHttpRequest();
  xmlhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      // Process the received data
      var csvData = this.responseText;
      var lines = csvData.split("\n"); // Split data into lines

      //! Skip the header line
      lines.shift(); // Remove the first element (header)

      //! Parse each data line
      lines.forEach(function(line) {
        var values = line.split(","); // Split line into values
        var timestamp = Date.parse(values[1] + " " + values[2]) + 7200000; // Parse date+time to timestamp
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
// Call on page load.
getTempHistory();

/**
 * Periodically (every 1 minute) gets the temperature from the sensor
 */
setInterval(function ( ) {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      var x = (new Date()).getTime() + 7200000,
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
}, 60000 ) ;


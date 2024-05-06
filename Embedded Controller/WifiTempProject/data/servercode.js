/**
 * @file
 * Contains the javascript to be run on the index page.
 */

//! Define a variable to store the last reading ID
let lastReadingID = -1;

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

// Fetch the checkbox element
var showAllReadingsCheckbox = document.getElementById('show-all-readings');

// Add event listener to the checkbox
showAllReadingsCheckbox.addEventListener('change', function() {
  if (this.checked) {
    // If checkbox is checked, update chart to show all readings
    updateChartData(null); // Pass null to indicate showing all readings
  } else {
    // If checkbox is unchecked, update chart based on slider value
    updateChartData(timeSlider.value);
  }
});

function updateChartData(hours) {
  var filteredTimestamps = [];
  var filteredTemperatures = [];

  // If hours is null, show all readings
  if (hours === null) {
    filteredTimestamps = allTimestamps;
    filteredTemperatures = allTemperatures;
  } else {
    // Otherwise, filter data based on slider value (last 'hours')
    var oneHour = 1000 * 60 * 60; // Milliseconds in an hour
    var currentTime = Date.now() + 7200000;
    var threshold = currentTime - (hours * oneHour);

    allTimestamps.forEach(function(timestamp, index) {
      if (timestamp >= threshold) {
        filteredTimestamps.push(timestamp);
        filteredTemperatures.push(allTemperatures[index]);
      }
    });
  }
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
      lines.forEach(function(line, index) {
        var values = line.split(","); // Split line into values
        var timestamp = Date.parse(values[1] + " " + values[2]) + 7200000; // Parse date+time to timestamp
        var temperature = parseFloat(values[3]); // Convert temperature to number

        allTimestamps.push(timestamp);
        allTemperatures.push(temperature);

        // Update lastReadingID with the ID of the last reading in the historical data
        if (index === lines.length - 1) {
          lastReadingID = parseInt(values[0]);
        }
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

// Create WebSocket connection
const socket = new WebSocket('ws://'+window.location.hostname+':81/');

// Connection opened
socket.addEventListener('open', function (event) {
  console.log('WebSocket connected');
});

// Listen for messages
socket.addEventListener('message', function (event) {
  console.log('Message from server ', event.data);
  // Handle received message here, update chart or do any other necessary action
  const [readingID, dateTime, temperature] = event.data.split(",");
  const x = new Date(dateTime).getTime() + 7200000;
  const y = parseFloat(temperature);
  
  // Check if the readingID is different from the last one
  if (readingID != lastReadingID) {
    // Add data to the graph
    allTemperatures.push(y);
    allTimestamps.push(x);
    if (chartT.series[0].data.length > 40) {
      chartT.series[0].addPoint([x, y], true, true, true);
    } else {
      chartT.series[0].addPoint([x, y], true, false, true);
    }
    
    // Update the lastReadingID
    lastReadingID = readingID;
  } else {
    console.log("Duplicate reading ID, skipping.");
  }
});

/**
 * Function to allow for deleting a single specific ID, primarily used during development to prune incorrect data logging.
 */
function deleteReading() {
  var readingID = document.getElementById('reading-id').value;
  var deleteMessageElement = document.getElementById('delete-message');

  // Send an XMLHttpRequest to the server to delete the reading
  var xhr = new XMLHttpRequest();
  xhr.onreadystatechange = function() {
    if (this.readyState == 4) {
      if (this.status == 200) {
        // Reading deleted successfully
        deleteMessageElement.textContent = "Reading with ID " + readingID + " deleted successfully.";
      } else {
        // Failed to delete reading
        deleteMessageElement.textContent = "Failed to delete reading with ID " + readingID + ".";
      }
    }
  };
  // Send a POST request to the server with the reading ID to delete
  xhr.open("POST", "/delete-reading", true);
  xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  xhr.send("readingID=" + readingID);
}

document.getElementById('uploadForm').addEventListener('submit', function(event) {
  event.preventDefault();

  var fileInput = document.getElementById('fileInput');
  var file = fileInput.files[0];
  if (!file) {
    return;
  }

  var formData = new FormData();
  formData.append('file', file);

  var xhr = new XMLHttpRequest();
  xhr.open('POST', '/upload', true);
  xhr.onload = function() {
    if (xhr.status === 200) {
      document.getElementById('status').textContent = xhr.responseText;
    } else {
      document.getElementById('status').textContent = 'Upload failed';
    }
  };
  xhr.send(formData);
});

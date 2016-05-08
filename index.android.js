//react-native run-android
//adb logcat *:S ReactNative:V ReactNativeJS:V
//adb reverse tcp:8081 tcp:8081

import {
  AppRegistry,
  Component,
  ScrollView,
  StyleSheet,
  Text,
  View,
  TouchableHighlight,
  DeviceEventEmitter,
  Alert,
  Vibration
} from 'react-native';
import React from 'react';
import Button from 'react-native-button';
import {
  SegmentedControls
} from 'react-native-radio-buttons'; 

var stops = require("./stops.json");
var alertMessage = "Get off at next stop!";

let distance = function(currentLat, currentLong, stationLat, stationLong) {
  var radlat1 = Math.PI * currentLat/180
  var radlat2 = Math.PI * stationLat/180
  var theta = currentLong-stationLong
  var radtheta = Math.PI * theta/180
  var dist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
  dist = Math.acos(dist)
  dist = dist * 180/Math.PI
  dist = dist * 60 * 1.1515
  console.log("distance: ", dist)
  return dist
}

class CalTrainApp extends Component {
  constructor(props) {
    super(props);
    this.watchID = null;
    this.state = {
      station: '',
      allStops: '',
      lastPosition: 'unknown',
      stationLat: '',
      stationLong: '',
      currentLong: '',
      currentLat: '',
      showList: false,
    };
  }

  componentDidMount() {
    this.watchID = navigator.geolocation.watchPosition(this.onWatchPosition.bind(this));
  }

  onWatchPosition(position) {
    let lastPosition = position;
    let currentLong = position.coords.longitude;
    let currentLat = position.coords.latitude;
    this.setState({lastPosition, currentLat, currentLong});
    console.log("currentLat: ", this.state.currentLat, " currentLong: ", this.state.currentLong, " stationLong: ", this.state.stationLong, " stationLat: ", this.state.stationLat);
    let dist = distance(currentLat, currentLong, this.state.stationLat, this.state.stationLong);
    if (dist <= 0.5) {
      console.log("you are so close!");
      Vibration.vibrate(
        [0, 500, 200, 500], true),
      Alert.alert(
        'Alert',
        alertMessage,
        [
          {text: 'OK', onPress: this._onAlertPressed.bind(this)}
        ]
      )
    }
  }

  _onAlertPressed() {
    Vibration.vibrate(),
    console.log("OK pressed!")

  }

  toggleList() {
    this.setState({
      showList: !this.state.showList
    });
  }

  _renderList() {
    const options = [
      "Option 1",
      "Option 2"
    ];

    function setLatAndLong(station){
      this.setState({
        station
      });
      let stops = this.state.allStops.stops
      for(i in stops) {
        if(station === stops[i].name){
          let stationLat = stops[i].lat;
          let stationLong = stops[i].long;
          this.setState({stationLong, stationLat});
          setTimeout(() => {
            this.setState({
              showList: false
            }); 
          }, 500);
        }
      }
    }
    if (this.state.showList) {
      return (
          <ScrollView>
            <SegmentedControls
              options={
                this.state.allStops.stops.map(function(stop) {
                  return stop.name
                }) 
              }
              onSelection={ setLatAndLong.bind(this) }
              selectedOption={ this.state.station }
              direction={ 'column' }
            />
          </ScrollView>
      );
    } else {
      return null;
    }
  } 

  componentWillUnmount() {
    navigator.geolocation.clearWatch(this.watchID);
  }

  componentWillMount() {
    this.setState({
      allStops: stops
    })
  }

  render() {
    return (
      <View style={styles.container}>
        <Button containerStyle={styles.buttons} style={{color: 'white'}} onPress={this.toggleList.bind(this)}>Pick Your Exit Station</Button>
        {this._renderList()}
        <Text style={styles.station}>{'\n' + this.state.station}</Text>
    </View>)
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
    margin: 20
  },
  buttons: {
    padding:10, 
    height:45, 
    overflow:'hidden', 
    borderRadius:4, 
    backgroundColor: 'firebrick'
  },
  station: {
    fontSize: 18,
    color: 'steelblue',
    fontWeight: 'bold'
  }
});

AppRegistry.registerComponent('CalTrainApp', () => CalTrainApp);

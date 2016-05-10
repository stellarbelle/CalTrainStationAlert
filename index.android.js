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
  DeviceEventEmitter,
  Alert,
  Vibration,
} from 'react-native';
import React from 'react';
import Button from 'react-native-button';
import AppAndroid from './AppAndroid';
import {
  SegmentedControls
} from 'react-native-radio-buttons';
import Sound from 'react-native-sound'; 

var stops = require("./stops.json");
var alertMessage = "Get off at next stop!";
var alertSound = new Sound('elegant_ringtone.mp3', Sound.MAIN_BUNDLE, (error) => {
  if (error) {
    console.log('failed to load the sound', error);
  } else {
    console.log('Sound loaded');
  }
});
alertSound.setNumberOfLoops(-1);

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
    // this.watchID = null;
    this.state = {
      station: '',
      allStops: '',
      // lastPosition: 'unknown',
      // stationLat: '',
      // stationLong: '',
      // currentLong: '',
      // currentLat: '',
      showList: false,
      distance: ''
    };
  }

  // componentDidMount() {
  //   this.watchID = navigator.geolocation.watchPosition(this.onWatchPosition.bind(this));
  // }

  onWatchPosition() {
    // let lastPosition = position;
    // let currentLong = position.coords.longitude;
    // let currentLat = position.coords.latitude;
    // this.setState({lastPosition, currentLat, currentLong});
    // console.log("currentLat: ", this.state.currentLat, " currentLong: ", this.state.currentLong, " stationLong: ", this.state.stationLong, " stationLat: ", this.state.stationLat);
    // let dist = distance(currentLat, currentLong, this.state.stationLat, this.state.stationLong);
    console.log("dist: ", this.state.distance);
    let dist = this.state.distance;
    if (dist <= 0.5) {
      console.log("you are so close!");
      alertSound.play((success) => {
        if (success) {
          console.log('successfully finished playing');
        } else {
          console.log('playback failed due to audio decoding errors');
        }
      });
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
    alertSound.stop(),
    console.log("OK pressed!")

  }

  toggleList() {
    this.setState({
      showList: !this.state.showList
    });
  }

  setLatAndLong(station){
    this.setState({
      station
    });
    let stops = this.state.allStops.stops
    for(i in stops) {
      if(station === stops[i].name){
        let stationLat = stops[i].lat;
        let stationLong = stops[i].long;
        AppAndroid.setStation(stationLat.toString(), stationLong.toString(), this.onLocationUpdated.bind(this));
        // this.setState({stationLong, stationLat});
        setTimeout(() => {
          this.setState({
            showList: false
          }); 
        }, 500);
      }
    }
  }

  onLocationUpdated(data) {
    this.setState({
      distance: data
    });
    console.log("I am updated! ", data);
    this.onWatchPosition.bind(this)
  }

  _renderList() {
    if (this.state.showList) {
      return (
          <ScrollView>
            <SegmentedControls
              options={
                this.state.allStops.stops.map(function(stop) {
                  return stop.name
                }) 
              }
              onSelection={ this.setLatAndLong.bind(this) }
              selectedOption={ this.state.station }
              direction={ 'column' }
            />
          </ScrollView>
      );
    } else {
      return null;
    }
  }

  _renderDistance() {
    if(this.state.distance) {
      return (
        <Text>{'\n'} You are { this.state.distance } miles away.</Text>
      )
    }
  } 

  // componentWillUnmount() {
  //   navigator.geolocation.clearWatch(this.watchID);
  // }

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
        {this._renderDistance()}
      </View>
    )
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

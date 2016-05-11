//react-native run-android
//adb logcat *:S ReactNative:V ReactNativeJS:V
//adb reverse tcp:8081 tcp:8081
//<Text>{this.state.eventSwitchRegressionIsOn ? 'On' : 'Off'}</Text>

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
  Switch
} from 'react-native';
import React from 'react';
import Button from 'react-native-button';
import AppAndroid from './AppAndroid';
import {
  SegmentedControls
} from 'react-native-radio-buttons';
import Sound from 'react-native-sound'; 
import SettingsList from 'react-native-settings-list';

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
      distance: '',
      audioSwitchValue: true,
      vibrateSwitchValue: true
    };
  }

  onAudioValueChange() {
    this.setState({
      audioSwitchValue: !this.state.audioSwitchValue
    }); 
  }

  onVibrateValueChange() {
    this.setState({
      vibrateSwitchValue: !this.state.vibrateSwitchValue
    }); 
  }

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
      if(this.state.audioSwitchValue) {
        alertSound.play((success) => {
          if (success) {
            console.log('successfully finished playing');
          } else {
            console.log('playback failed due to audio decoding errors');
          }
        });
      }
      if (this.state.vibrateSwitchValue) {
        Vibration.vibrate(
        [0, 500, 200, 500], true)
      }
      Alert.alert(
        'Alert',
        alertMessage,
        [
          {text: 'OK', onPress: this.onAlertPressed.bind(this)}
        ]
      )
    }
  }

  onAlertPressed() {
    if(this.state.vibrateSwitchValue) {
      Vibration.cancel()
    }
    if(this.state.audioSwitchValue) {
      alertSound.stop()
    }
    console.log("OK pressed!")
  }

  onLocationUpdated(data) {
    this.setState({
      distance: data
    });
    console.log("I am updated! ", data);
    this.onWatchPosition.bind(this)
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
        }, 600);
      }
    }
  }

  componentWillMount() {
    this.setState({
      allStops: stops
    })
  }

  // componentDidMount() {
  //   this.watchID = navigator.geolocation.watchPosition(this.onWatchPosition.bind(this));
  // }

  // componentWillUnmount() {
  //   navigator.geolocation.clearWatch(this.watchID);
  // }

  toggleList() {
    this.setState({
      station: '',
      showList: !this.state.showList,
    });
  }

  _renderSwitch() {
    if (!this.state.showList) {
      return (
        <View>
          <View style={styles.switch} >
            <Text style={styles.label}>Vibrate</Text>
            <Switch
              onValueChange={this.onVibrateValueChange.bind(this)}
              style={{marginBottom: 10}}
              value={this.state.vibrateSwitchValue} />
              <Text style={styles.label}>{this.state.vibrateSwitchValue ? 'On' : 'Off'}{'\n'}</Text>
          </View>
          <View style={styles.switch}>
            <Text style={styles.label}>Audio</Text>
            <Switch
              onValueChange={this.onAudioValueChange.bind(this)}
              style={{marginBottom: 10}}
              value={this.state.audioSwitchValue} />
            <Text style={styles.label}>{this.state.audioSwitchValue ? 'On' : 'Off'}</Text>
          </View>
        </View>
      );
    } else {
      return null;
    }
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
            tint={'steelblue'}
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

  _renderStation() {
    if(this.state.station && !this.state.showList) {
      return (
        <View style={styles.info}>
          <Text style={{fontSize: 16}}>{'\n\n'}Your station is:</Text>
          <Text style={styles.station}>{this.state.station}</Text>
          <Text>You are { this.state.distance } miles away.</Text>
        </View>
      );
    }
  } 

  render() {
    return (
      <View style={styles.container}>
        {this._renderSwitch()}
        <Text>{'\n\n\n'}</Text>
        <Button containerStyle={styles.buttons} style={{color: 'white'}} onPress={this.toggleList.bind(this)}>Pick Your Exit Station</Button>
        <Text>{'\n'}</Text>
        {this._renderList()}
        {this._renderStation()}
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
    fontWeight: 'bold',
    textAlign: 'center',
    marginTop: 10,
    marginBottom: 10
  },
  switch: {
    flexDirection: 'row',
  },
  label: {
    textAlign: 'center',
    textAlignVertical: 'auto',
    marginTop: 2
  },
  info: {
    alignItems: 'center'
  }
});

AppRegistry.registerComponent('CalTrainApp', () => CalTrainApp);

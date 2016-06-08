//react-native run-android & react-native start
//adb logcat *:S ReactNative:V ReactNativeJS:V TestingActivity:V TestingService:V AppModule:V
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
import Subscribable from 'Subscribable';
import reactMixin from 'react-mixin';
import Radio, {
  Option
} from 'react-native-radio-button-classic'

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

class CalTrainApp extends Component {
  constructor(props) {
    super(props);
    this.state = {
      station: '',
      allStops: '',
      showList: false,
      distance: '',
      audioSwitchValue: true,
      vibrateSwitchValue: true,
      alert: false,
      optionSelected: 1
    };
  }

  onAudioValueChange() {
    this.setState({
      audioSwitchValue: !this.state.audioSwitchValue
    });
    AppAndroid.setAudio(this.state.audioSwitchValue);
  }

  onVibrateValueChange() {
    this.setState({
      vibrateSwitchValue: !this.state.vibrateSwitchValue
    });
    AppAndroid.setVibrate(this.state.vibrateSwitchValue); 
  }

  onWatchPosition() {
    console.log("alert outside: ", this.state.alert);
    console.log("dist: ", this.state.distance);
    let dist = this.state.distance;
    if (this.state.alert === true) {
      console.log("alert: ", this.state.alert);
      setTimeout(() => {
        console.log("inside timout!!!");
        this.setState ({
          stationLat: '',
          stationLong: '',
          station: ''
        });
        console.log("you are so close!");
        // if(this.state.audioValue) {
        //   alertSound.play((success) => {
        //     if (success) {
        //       console.log('successfully finished playing');
        //     } else {
        //       console.log('playback failed due to audio decoding errors');
        //     }
        //   });
        // }
        if (this.state.vibrateValue) {
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
      }, 600);
    } else if (dist <= 0.5) {
      this.setState ({
        stationLat: '',
        stationLong: '',
        station: ''
      });
      console.log("you are so close!");
      if(this.state.audioValue) {
        alertSound.play((success) => {
          if (success) {
            console.log('successfully finished playing');
          } else {
            console.log('playback failed due to audio decoding errors');
          }
        });
      }
      if (this.state.vibrateValue) {
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

  onSelect(index) {
    this.setState({
      optionSelected: index + 1
    });
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

  setLatAndLong(station){
    this.setState({
      station
    });
    let stops = this.state.allStops.stops
    for(i in stops) {
      if(station === stops[i].name){
        let stationLat = stops[i].lat;
        let stationLong = stops[i].long;
        AppAndroid.setStation(stationLat.toString(), stationLong.toString());
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
    });
    DeviceEventEmitter.addListener('updatedDistance', function(e: Event) {
      console.log("I am updated! ", e.distance, e.audioValue, e.vibrateValue);
      this.setState({
        distance: e.distance,
        audioValue: e.audioValue,
        vibrateValue: e.vibrateValue,
        alert: e.alert
      });
      this.onWatchPosition();
    }.bind(this));
  }

  componentWillUnmount() {
    DeviceEventEmitter.removeListener('updatedDistance', function(e: Event) {
      console.log("I am updated! ", e.distance, e.audioValue, e.vibrateValue);
      this.setState({
        distance: e.distance,
        audioValue: e.audioValue,
        vibrateValue: e.vibrateValue,
        alert: e.alert
      });
      this.onWatchPosition();
    }.bind(this));
  }

  toggleList() {
    this.setState({
      station: '',
      showList: !this.state.showList,
    });
  }

  _renderSwitch() {
    if (!this.state.showList) {
      return (
        <View style={styles.switchBlock}>
          <View>
            <Text style={styles.switchLabel}>Vibrate</Text>
            <Text style={styles.switchLabel}>Audio</Text>
          </View>
          <View style={styles.switchCol}>
            <View style={styles.switchRow}>
              <Switch
                onValueChange={this.onVibrateValueChange.bind(this)}
                style={styles.switch}
                value={this.state.vibrateSwitchValue} />
                <Text style={styles.label}>{this.state.vibrateSwitchValue ? 'On' : 'Off'}{'\n'}</Text>
            </View>
            <View style={styles.switchRow}>
              <Switch
                onValueChange={this.onAudioValueChange.bind(this)}
                style={styles.switch}
                value={this.state.audioSwitchValue} />
              <Text style={styles.label}>{this.state.audioSwitchValue ? 'On' : 'Off'}</Text>
            </View>
          </View>
        </View>
      );
    } else {
      return null;
    }
  }

  _renderMileButtons() {
    return(
      <View>
        <Radio onSelect={this.onSelect.bind(this)} defaultSelect={this.state.optionSelected - 1}>
          <Option color="lightseagreen" selectedColor="darkcyan">
            <Item title="0.5 Miles"/>
          </Option>
          <Option color="lightseagreen" selectedColor="darkcyan">
            <Item title="1 Mile"/>
          </Option>
          <Option color="lightseagreen" selectedColor="darkcyan">
            <Item title="3 Miles"/>
          </Option>
          <Option color="lightseagreen" selectedColor="darkcyan">
            <Item title="5 Miles"/>
          </Option>
        </Radio>
      </View>
    );
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
        {this._renderMileButtons()}
        {this._renderStation()}
      </View>
    )
  }
}
reactMixin(CalTrainApp.prototype, Subscribable);

class Item extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    var { title, description } = this.props;

    return (
      <View style={{ paddingTop: 7, paddingLeft: 8 }}>
        <Text style={styles.title}>{ title }</Text>
        <Text style={styles.description}>{ description }</Text>
      </View>
    );
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
  switchBlock: {
    flexDirection: 'row'
  },
  switch: {
    marginBottom: 10, 
    alignItems: 'flex-end'
  },
  switchRow: {
    flexDirection: 'row'
  },
  switchCol: {
    flexDirection: 'column'
  },
  switchLabel: {
    marginBottom: 16,
    marginTop: 2
  },
  label: {
    textAlignVertical: 'auto',
    marginTop: 2,
    marginLeft: 8
  },
  info: {
    alignItems: 'center'
  },
  title: {
  }
});

AppRegistry.registerComponent('CalTrainApp', () => CalTrainApp);

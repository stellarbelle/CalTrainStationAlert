//react-native run-android & react-native start
//adb logcat *:S ReactNative:V ReactNativeJS:V TestingActivity:V TestingService:V AppModule:V
//adb reverse tcp:8081 tcp:8081
//react-native-radio-buttons\lib\segmented-controls.js
//#009385, #abdddb

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
  Switch,
  Dimensions
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
import Radio, {RadioButton, RadioButtonInput, RadioButtonLabel} from 'react-native-simple-radio-button';

var { width, height } = Dimensions.get('window');

var stops = require("./stops.json");

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
      minuteSelected: 1,
      value: 0,
      oneMin: true,
      threeMin: false,
      fiveMin: false
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

  
  onSelectMinutes(index) {
    if (index == 1) {
      this.setState({
        minuteSelected: 1,
        oneMin: true,
        threeMin: false,
        fiveMin: false
      });
      console.log("index: ",index)
      AppAndroid.setMinuteAlert(this.state.minuteSelected); 
    } else if (index == 3) {
      this.setState({
        minuteSelected: 3,
        threeMin: true,
        fiveMin: false,
        oneMin: false
      });
      console.log("index: ",index)
      AppAndroid.setMinuteAlert(this.state.minuteSelected);
    } else {
      this.setState({
        minuteSelected: 5,
        fiveMin: true,
        threeMin: false,
        oneMin: false,
      });
      console.log("index: ",index)
      AppAndroid.setMinuteAlert(this.state.minuteSelected);
    }
    console.log("onSelect index ", index)
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
      console.log("I am updated! ", e.distance);
      this.setState({
        distance: e.distance,
        alert: e.alert
      });
    }.bind(this));
  }

  componentWillUnmount() {
    DeviceEventEmitter.removeListener('updatedDistance', function(e: Event) {
      console.log("I am updated! ", e.distance);
      this.setState({
        distance: e.distance,
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

  showTones() {
    AppAndroid.setTone(true);
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

  _renderMinuteButtons() {
    if (!this.state.showList) {
      return(
        <View style={styles.warningButtons}>
          <RadioButton>
            <RadioButtonInput 
                onPress={this.onSelectMinutes.bind(this)}
                buttonOuterColor='#abdddb' 
                buttonInnerColor='#009385'
                buttonSize={13}
                index={0}
                obj={{value: '1'}}
                isSelected={this.state.oneMin}
            />
            <RadioButtonLabel 
                onPress={this.onSelectMinutes.bind(this)}
                labelColor='grey'
                index={0}
                obj={{label: '  1 Minute Warning', value: '1'}} 
            >
            </RadioButtonLabel>
          </RadioButton>
          <RadioButton>
            <RadioButtonInput 
                onPress={this.onSelectMinutes.bind(this)}
                buttonOuterColor='#abdddb' 
                buttonInnerColor='#009385'
                buttonSize={13}
                index={1}
                obj={{value: '3'}}
                isSelected={this.state.threeMin} 
            />
            <RadioButtonLabel 
                onPress={this.onSelectMinutes.bind(this)}
                labelColor='grey'
                index={1}
                obj={{label: '  3 Minute Warning', value: '3'}} 
            >
            </RadioButtonLabel>
          </RadioButton>
          <RadioButton>
            <RadioButtonInput 
                onPress={this.onSelectMinutes.bind(this)}
                buttonOuterColor='#abdddb' 
                buttonInnerColor='#009385'
                buttonSize={13}
                index={2}
                obj={{value: '5'}}
                isSelected={this.state.fiveMin}
            />
            <RadioButtonLabel 
                onPress={this.onSelectMinutes.bind(this)}
                labelColor='grey'
                index={2}
                obj={{label: '  5 Minute Warning', value: '5'}} 
            >
            </RadioButtonLabel>
          </RadioButton>
        </View>
      );
    }
  }

  _renderList() {
    if (this.state.showList) {
      return (
        <View style={styles.stationList}>
          <ScrollView style={{height: height-140}}>
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
        </View>
      );
    } else {
      return (
        <View style={styles.stationList}>
          <Button containerStyle={styles.buttons} style={{color: 'white'}} onPress={this.toggleList.bind(this)}>Pick Your Exit Station</Button>
        </View>
      );
    }
  }
  _renderToneMenu() {
    return (
      <View>
        <Button containerStyle={styles.menu} style={{color: 'white', fontSize: 14}}
                onPress={this.showTones.bind(this)}>Pick a Tone</Button>
      </View>
    );
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
        {this._renderToneMenu()}
        {this._renderSwitch()}
        <Text>{'\n\n\n'}</Text>
        {this._renderList()}
        <Text>{'\n'}</Text>
        {this._renderMinuteButtons()}
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
      <View style={{ paddingLeft: 8 }}>
        <Text style={styles.title}>{ title }</Text>
        <Text style={styles.description}>{ description }</Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    // justifyContent: 'center',
    // alignItems: 'center',
    backgroundColor: '#F5FCFF',
    // marginTop: 90
  },
  overlay: {
    flex: 1,
    position: 'absolute',
    left: 0,
    top: 20,
    opacity: 0.5,
    backgroundColor: 'black',
    width: width,
    height: height
  },
  menu: {
    top: 0,
    position: 'absolute',
    height: 20,
    backgroundColor: 'grey',
    width: width
  },
  buttons: {
    width:200,
    padding:10, 
    height:45, 
    overflow:'hidden', 
    borderRadius:4, 
    backgroundColor: 'firebrick'
  },
  stationList: {
    marginTop: 0,
    justifyContent: 'center',
    alignItems: 'center',
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
    marginTop: 90,
    justifyContent: 'center',
    alignItems: 'center',
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
  warningButtons: {
    marginTop: 35
  }
});

AppRegistry.registerComponent('CalTrainApp', () => CalTrainApp);

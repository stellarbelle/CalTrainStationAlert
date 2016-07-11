//react-native run-android & react-native start
//adb logcat *:S ReactNative:V ReactNativeJS:V TestingActivity:V TestingService:V AppModule:V

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
import Radio, {
  RadioButton, 
  RadioButtonInput, 
  RadioButtonLabel
} from 'react-native-simple-radio-button';

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
      selected: 'oneMin'
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
        selected: 'oneMin',
      });
      console.log("index: ",index)
      AppAndroid.setMinuteAlert(this.state.minuteSelected); 
    } else if (index == 3) {
      this.setState({
        minuteSelected: 3,
        selected: 'threeMin',
      });
      console.log("index: ",index)
      AppAndroid.setMinuteAlert(this.state.minuteSelected);
    } else {
      this.setState({
        minuteSelected: 5,
        selected: 'fiveMin',
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
    AppAndroid.setTone();
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
      console.log("isSelectedButtons: " + this.state.selected === 'oneMin');
      return(
        <View style={styles.warningButtons}>
          <MyButton 
              index={0} 
              onSelect={this.onSelectMinutes.bind(this)}
              isSelected={this.state.selected === 'oneMin'} 
              inputObj={{value: 1}} 
              labelObj={{label: '  1 Minute Warning', value: '1'}}
          />
          <MyButton 
              index={1}
              onSelect={this.onSelectMinutes.bind(this)} 
              isSelected={this.state.selected === 'threeMin'} 
              inputObj={{value: 3}} 
              labelObj={{label: '  3 Minute Warning', value: '3'}}
          />
          <MyButton 
              index={2} 
              onSelect={this.onSelectMinutes.bind(this)}
              isSelected={this.state.selected === 'fiveMin'} 
              inputObj={{value: 5}} 
              labelObj={{label: '  5 Minute Warning', value: '5'}}/>
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
              tint={'#009385'}
              selectedTint={'#abdddb'}
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
    if(!this.state.showList) {
      return (
        <View>
          <Button containerStyle={styles.menu} style={{color: '#abdddb', fontSize: 14}}
                  onPress={this.showTones.bind(this)}>Pick a Tone</Button>
        </View>
      );
    }
  }
  _renderStation() {
    if(this.state.station && !this.state.showList) {
      return (
        <View style={styles.info}>
          <Text style={{fontSize: 16}}>{'\n\n'}Your station is:</Text>
          <Text style={styles.station}>{this.state.station}</Text>
          {this._renderDistance()}
        </View>
      );
    }
  } 
  _renderDistance() {
    if(this.state.distance) {
      return(
        <View style={styles.info}>
          <Text>You are { this.state.distance } minutes away.</Text>
        </View>
      );
    } else {
      return(
        <View style={styles.info}>
          <Text>You are on your way!</Text>
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
    );
  }
}
reactMixin(CalTrainApp.prototype, Subscribable);

class MyButton extends Component {
    constructor(props) {
    super(props);
  }

  render() {
    var { index, onSelect, isSelected, inputObj, labelObj } = this.props;


    return (
      <RadioButton>
        <RadioButtonInput 
            onPress={onSelect}
            buttonOuterColor='#abdddb' 
            buttonInnerColor='#009385'
            buttonSize={13}
            index={index}
            obj={inputObj}
            isSelected={isSelected}
        />
        <RadioButtonLabel 
            onPress={onSelect}
            labelColor='grey'
            index={index}
            obj={labelObj} 
        >
        </RadioButtonLabel>
      </RadioButton>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5FCFF',
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
    top: 10,
    position: 'absolute',
    height: 25,
    borderColor: '#abdddb',
    borderWidth: 2,
    borderRadius: 5,
    marginLeft: 10,
    backgroundColor: '#009385',
    width: width - 20
  },
  buttons: {
    width:200,
    padding:10, 
    height:45, 
    overflow:'hidden', 
    borderRadius:4, 
    backgroundColor: '#cd5c5c'
  },
  stationList: {
    marginTop: 0,
    justifyContent: 'center',
    alignItems: 'center',
  },
  station: {
    fontSize: 18,
    color: '#009385',
    fontWeight: 'bold',
    textAlign: 'center',
    marginTop: 10,
    marginBottom: 10
  },
  switchBlock: {
    marginTop: 100,
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

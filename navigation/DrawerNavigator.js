import React from 'react';
import {View, Text} from 'react-native';
import {createDrawerNavigator} from 'react-navigation-drawer';

import HomeScreen from '../screens/HomeScreen';

const DrawerNavigator = createDrawerNavigator({
  Settings: HomeScreen,
  "Developer's list": HomeScreen,
  'Help and support': HomeScreen,
});

export default DrawerNavigator;

import React from 'react';
import {View, Text, StyleSheet} from 'react-native';

// pull in from DrawerTrigger.js
import DrawerTrigger from './DrawerTrigger';

class Header extends React.Component {
  render() {
    return (
      <View style={styles.header}>
        <DrawerTrigger />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  header: {
    height: 80,
    paddingTop: 40,
  },
});

export default Header;

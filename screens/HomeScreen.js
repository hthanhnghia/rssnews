import React from 'react';
import {
  View,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Text,
  Linking,
} from 'react-native';
import {WebView} from 'react-native-webview';
import LinearGradient from 'react-native-linear-gradient';

import Header from '../components/Header.js';
import DataFetch from '../nativeModules/DataFetch';

function Item({title, content, published, updated, author}) {
  let htmlContent =
    '<style> \
        img { display: block; width: 100%; min-height: 350px; } \
        p {font-size: 50px; color: white; font-family: "Avenir Next";	font-weight: 600; text-overflow: ellipsis;word-wrap: break-word;overflow: hidden; max-height: 120;} \
      </style>' +
    content;
  return (
    <View style={styles.item}>
      <View style={styles.headerContainer}>
        <Text style={styles.header}>{author}</Text>
        <Text style={styles.header}>{published.split('T')[0]}</Text>
      </View>

      <WebView
        style={styles.webview}
        useWebKit
        originWhitelist={['*']}
        source={{html: htmlContent}}
      />
    </View>
  );
}

function RecommendedItem({title, content, published, updated, author}) {
  let htmlContent =
    '<style> \
        img { display: block; width: 100%; height: 100%; } \
        p {font-size: 50px; color: white; font-family: "Avenir Next";	font-weight: 600; text-overflow: ellipsis;word-wrap: break-word;overflow: hidden; max-height: 120;} \
      </style>' +
    content;
  return (
    <View style={styles.recommendedItem}>
      <View style={styles.headerContainer}>
        <Text style={styles.header}>{author}</Text>
      </View>

      <WebView
        style={styles.webview}
        useWebKit
        originWhitelist={['*']}
        source={{html: htmlContent}}
      />
    </View>
  );
}

export default class HomeScreen extends React.Component {
  static navigationOptions = {};
  constructor(props) {
    super(props);
    this.state = {
      feeds: [],
    };
  }

  componentDidMount() {
    const fetchData = async () => {
      try {
        const data = await DataFetch.fetchRssNews();
        const jsonObj = JSON.parse(data);

        const index = jsonObj.main.length / 2;

        const feeds = [
          ...jsonObj.main.slice(0, index),
          jsonObj.recommended,
          ...jsonObj.main.slice(index),
        ];

        this.setState({
          feeds: feeds,
        });
      } catch (e) {
        console.error(e);
      }
    };
    fetchData();
  }

  render() {
    return (
      <LinearGradient colors={['#504718', '#000000']} style={{flex: 1}}>
        <Header />
        <View style={styles.container}>
          <FlatList
            style={{width: '100%'}}
            data={this.state.feeds}
            renderItem={({item}) => {
              if (item instanceof Array) {
                return (
                  <View>
                    <Text
                      style={styles.recommendedArticlesHeader}
                      key="recommended-header">
                      RECOMMENDED ARTICLES
                    </Text>
                    <FlatList
                      style={{width: '100%'}}
                      showsHorizontalScrollIndicator={false}
                      horizontal
                      data={item}
                      renderItem={({item}) => {
                        return (
                          <RecommendedItem
                            title={item.title}
                            content={item.content}
                            published={item.published}
                            updated={item.updated}
                            author={item.author}
                          />
                        );
                      }}
                      keyExtractor={item => item.title}
                    />
                  </View>
                );
              } else {
                return (
                  <Item
                    title={item.title}
                    content={item.content}
                    published={item.published}
                    updated={item.updated}
                    author={item.author}
                  />
                );
              }
            }}
            keyExtractor={item => item.title}
          />
        </View>
      </LinearGradient>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  item: {
    flex: 1,
    backgroundColor: '#000000',
    padding: 20,
    marginVertical: 8,
    marginHorizontal: 16,
  },
  recommendedItem: {
    width: 250,
    height: 250,
    backgroundColor: '#000000',
    padding: 20,
    marginVertical: 8,
    marginHorizontal: 16,
  },
  recommendedArticlesHeader: {
    padding: 20,
    paddingBottom: 10,
    fontFamily: 'Avenir Next',
    fontSize: 25,
    color: 'white',
  },
  headerContainer: {
    flex: 1,
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  header: {
    color: '#6D7278',
    fontFamily: 'Avenir Next',
    fontSize: 15,
    paddingBottom: 17,
  },
  webview: {
    width: '100%',
    height: 300,
    backgroundColor: '#000000',
  },
});

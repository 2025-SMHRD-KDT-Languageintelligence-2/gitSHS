import React, { useState, useEffect } from "react";
import { Container, Card, CardHeader, CardMedia, CardContent, CardActions, Avatar, IconButton, Typography } from "@mui/material";
import { Favorite, FavoriteBorder, ChatBubbleOutline, Send } from "@mui/icons-material";
import "../component/style/Feed.css"; // 스타일 파일 추가

// 기본 게시물 데이터
const initialPosts = [
  { id: 1, user: "user1", image: "https://source.unsplash.com/random/400x400?sig=1", liked: false, likes: 12, caption: "첫 번째 게시물!" },
  { id: 2, user: "user2", image: "https://source.unsplash.com/random/400x400?sig=2", liked: false, likes: 25, caption: "멋진 풍경 🌅" },
  { id: 3, user: "user3", image: "https://source.unsplash.com/random/400x400?sig=3", liked: false, likes: 8, caption: "귀여운 고양이 🐱" },
];

const Feed = () => {
  const [posts, setPosts] = useState(initialPosts);

  // 좋아요 토글 함수
  const toggleLike = (id) => {
    setPosts(posts.map(post =>
      post.id === id ? { ...post, liked: !post.liked, likes: post.liked ? post.likes - 1 : post.likes + 1 } : post
    ));
  };

  // 무한 스크롤 (새로운 데이터 로드)
  useEffect(() => {
    const handleScroll = () => {
      if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 50) {
        const newPosts = initialPosts.map(post => ({
          ...post,
          id: post.id + posts.length,
        }));
        setPosts(prev => [...prev, ...newPosts]);
      }
    };

    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, [posts]);

  return (
    <Container maxWidth="sm" className="feed-container">
      {posts.map((post) => (
        <Card key={post.id} className="feed-card">
          {/* 유저 정보 */}
          <CardHeader
            avatar={<Avatar>{post.user[0].toUpperCase()}</Avatar>}
            title={post.user}
            subheader="2시간 전"
          />

          {/* 이미지 */}
          <CardMedia component="img" height="400" image={post.image} alt="Post" />

          {/* 캡션 */}
          <CardContent>
            <Typography variant="body1">{post.caption}</Typography>
          </CardContent>

          {/* 액션 버튼 (좋아요, 댓글, 공유) */}
          <CardActions disableSpacing>
            <IconButton onClick={() => toggleLike(post.id)} color={post.liked ? "error" : "default"}>
              {post.liked ? <Favorite /> : <FavoriteBorder />}
            </IconButton>
            <Typography variant="body2">{post.likes}</Typography>

            <IconButton>
              <ChatBubbleOutline />
            </IconButton>
            <IconButton>
              <Send />
            </IconButton>
          </CardActions>
        </Card>
      ))}
    </Container>
  );
};

export default Feed;

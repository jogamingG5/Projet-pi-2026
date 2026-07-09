# Frontend multi-stage Dockerfile
FROM node:18-alpine AS build
WORKDIR /app
COPY front/streetleague/package*.json ./
RUN npm ci
COPY front/streetleague .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist/streetleague /usr/share/nginx/html
COPY front/nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

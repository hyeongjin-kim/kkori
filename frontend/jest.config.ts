export default {
  testEnvironment: 'jsdom',
  preset: 'ts-jest',
  moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx', 'json', 'node'],
  moduleNameMapper: {
    '^.+\\.svg$': 'jest-svg-transformer',
    '\\.(css|less|sass|scss)$': 'identity-obj-proxy',
    '@/(.*)': '<rootDir>/src/$1',
    '@pages/*': '<rootDir>/src/pages/*',
    '@app/*': '<rootDir>/src/app/*',
    '@features/*': '<rootDir>/src/features/*',
    '@entities/*': '<rootDir>/src/entities/*',
    '@shared/*': '<rootDir>/src/shared/*',
    '@widgets/*': '<rootDir>/src/widgets/*',
  },
  setupFilesAfterEnv: ['<rootDir>/jest.setup.ts'],
};
